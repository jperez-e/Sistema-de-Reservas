import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { catchError, finalize, of, switchMap, tap } from 'rxjs';

import { Reserva } from '../../models/reserva.interface';
import { ReservaService } from '../../services/reserva.service';
import { ReservaFormComponent } from './reserva-form/reserva-form.component';

@Component({
  selector: 'app-reservas-page',
  imports: [ReservaFormComponent],
  templateUrl: './reservas-page.component.html',
  styleUrl: './reservas-page.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReservasPageComponent {
  private readonly reservaService = inject(ReservaService);
  private readonly reloadTick = signal(0);
  protected readonly loading = signal(false);
  protected readonly errorMessage = signal('');
  protected readonly cancelingId = signal<number | null>(null);

  private readonly reservasStream$ = toObservable(this.reloadTick).pipe(
    tap(() => {
      this.loading.set(true);
      this.errorMessage.set('');
    }),
    switchMap(() =>
      this.reservaService.getReservas().pipe(
        catchError(() => {
          this.errorMessage.set('No se pudieron cargar las reservas.');
          return of<Reserva[]>([]);
        }),
        finalize(() => this.loading.set(false))
      )
    )
  );

  protected readonly reservas = toSignal(this.reservasStream$, {
    initialValue: [] as Reserva[]
  });

  protected readonly hasError = computed(() => this.errorMessage().length > 0);

  protected loadReservas(): void {
    this.reloadTick.update((value) => value + 1);
  }

  protected cancelReserva(reserva: Reserva): void {
    if (reserva.estado === 'CANCELADA' || this.cancelingId() === reserva.id) {
      return;
    }

    this.cancelingId.set(reserva.id);
    this.reservaService.cancelReserva(reserva.id).pipe(
      finalize(() => this.cancelingId.set(null))
    ).subscribe({
      next: () => {
        this.loadReservas();
      },
      error: () => {
        this.errorMessage.set('No se pudo cancelar la reserva.');
      }
    });
  }
}

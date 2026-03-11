import { ChangeDetectionStrategy, Component, computed, inject, output, signal } from '@angular/core';
import { ReactiveFormsModule, Validators, FormBuilder } from '@angular/forms';
import { finalize } from 'rxjs';

import { Reserva, ReservaCreate } from '../../../models/reserva.interface';
import { ReservaService } from '../../../services/reserva.service';
import { ToastComponent } from '../../../shared/components/toast/toast.component';

@Component({
  selector: 'app-reserva-form',
  imports: [ReactiveFormsModule, ToastComponent],
  templateUrl: './reserva-form.component.html',
  styleUrl: './reserva-form.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReservaFormComponent {
  private readonly reservaService = inject(ReservaService);
  private readonly formBuilder = inject(FormBuilder);

  protected readonly created = output<Reserva>();
  protected readonly saving = signal(false);
  protected readonly errorMessage = signal('');

  protected readonly servicios = signal([
    'Consultoría de software',
    'Arquitectura de soluciones cloud',
    'Automatización CI/CD',
    'Optimización de infraestructura',
    'Auditoría de seguridad'
  ]);

  protected readonly form = this.formBuilder.nonNullable.group({
    nombreCliente: ['', Validators.required],
    fecha: ['', Validators.required],
    hora: ['', Validators.required],
    servicio: ['', Validators.required]
  });

  protected readonly nombreCliente = this.form.controls.nombreCliente;
  protected readonly fecha = this.form.controls.fecha;
  protected readonly hora = this.form.controls.hora;
  protected readonly servicio = this.form.controls.servicio;

  protected readonly showToast = computed(() => this.errorMessage().length > 0);

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.errorMessage.set('');

    const payload: ReservaCreate = {
      ...this.form.getRawValue(),
      estado: 'ACTIVA'
    };

    this.saving.set(true);
    this.reservaService.createReserva(payload).pipe(
      finalize(() => this.saving.set(false))
    ).subscribe({
      next: (reserva) => {
        this.form.reset({
          nombreCliente: '',
          fecha: '',
          hora: '',
          servicio: ''
        });
        this.created.emit(reserva);
      },
      error: (error: unknown) => {
        this.errorMessage.set(this.resolveErrorMessage(error));
      }
    });
  }

  protected dismissToast(): void {
    this.errorMessage.set('');
  }

  private resolveErrorMessage(error: unknown): string {
    if (typeof error === 'string') {
      return error;
    }

    if (error && typeof error === 'object') {
      const payload = error as { error?: unknown; message?: unknown };
      if (typeof payload.error === 'string') {
        return payload.error;
      }
      if (typeof payload.message === 'string') {
        return payload.message;
      }
    }

    return 'No se pudo guardar la reserva.';
  }
}

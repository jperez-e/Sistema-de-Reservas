import { ChangeDetectionStrategy, Component, computed, effect, inject, input, output, signal } from '@angular/core';
import { ReactiveFormsModule, Validators, FormBuilder } from '@angular/forms';
import { finalize } from 'rxjs';

import { Reserva, ReservaCreate } from '../../../models/reserva.interface';
import { ReservaService } from '../../../services/reserva.service';
import { ToastComponent } from '../../../shared/components/toast/toast.component';

@Component({
  selector: 'app-reserva-form',
  standalone: true,
  imports: [ReactiveFormsModule, ToastComponent],
  templateUrl: './reserva-form.component.html',
  styleUrl: './reserva-form.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReservaFormComponent {
  private readonly reservaService = inject(ReservaService);
  private readonly formBuilder = inject(FormBuilder);

  readonly reservaToEdit = input<Reserva | null>(null);
  readonly saved = output<Reserva>();
  readonly cancelEdit = output<void>();
  readonly saving = signal(false);
  readonly errorMessage = signal('');

  readonly servicios = signal([
    'Consultoría de software',
    'Arquitectura de soluciones cloud',
    'Automatización CI/CD',
    'Optimización de infraestructura',
    'Auditoría de seguridad'
  ]);

  readonly form = this.formBuilder.nonNullable.group({
    nombreCliente: ['', Validators.required],
    fecha: ['', Validators.required],
    hora: ['', Validators.required],
    servicio: ['', Validators.required]
  });

  readonly nombreCliente = this.form.controls.nombreCliente;
  readonly fecha = this.form.controls.fecha;
  readonly hora = this.form.controls.hora;
  readonly servicio = this.form.controls.servicio;

  public readonly isEditing = computed(() => !!this.reservaToEdit());
  public readonly showToast = computed(() => this.errorMessage().length > 0);

  constructor() {
    effect(() => {
      const reserva = this.reservaToEdit();
      if (!reserva) {
        this.resetForm();
        return;
      }

      this.form.reset({
        nombreCliente: reserva.nombreCliente,
        fecha: reserva.fecha,
        hora: reserva.hora,
        servicio: reserva.servicio
      });
    });
  }

  public submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.errorMessage.set('');

    const payload: ReservaCreate = {
      ...this.form.getRawValue(),
      estado: 'ACTIVA'
    };

    const editingReserva = this.reservaToEdit();
    const request$ = editingReserva
      ? this.reservaService.updateReserva(editingReserva.id, payload)
      : this.reservaService.createReserva(payload);

    this.saving.set(true);
    request$.pipe(
      finalize(() => this.saving.set(false))
    ).subscribe({
      next: (reserva) => {
        this.resetForm();
        this.saved.emit(reserva);
      },
      error: (error: unknown) => {
        this.errorMessage.set(this.resolveErrorMessage(error));
      }
    });
  }

  public dismissToast(): void {
    this.errorMessage.set('');
  }

  public cancelEditing(): void {
    this.resetForm();
    this.cancelEdit.emit();
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

  private resetForm(): void {
    this.form.reset({
      nombreCliente: '',
      fecha: '',
      hora: '',
      servicio: ''
    });
  }
}

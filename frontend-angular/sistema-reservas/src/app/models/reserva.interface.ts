export interface Reserva {
  id: number;
  nombreCliente: string;
  fecha: string;
  hora: string;
  servicio: string;
  estado: 'ACTIVA' | 'CANCELADA';
}

export type ReservaCreate = Omit<Reserva, 'id'>;

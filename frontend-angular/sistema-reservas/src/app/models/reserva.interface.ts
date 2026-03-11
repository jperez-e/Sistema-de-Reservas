export interface Reserva {
  id: number;
  nombreCliente: string;
  fecha: string;
  hora: string;
  servicio: string;
  estado: 'ACTIVA' | 'CANCELADA';
}
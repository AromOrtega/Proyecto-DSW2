export type CitaEstado = 'PROGRAMADA' | 'CANCELADA' | 'COMPLETADA';

export interface Cita {
    id?: number;
    pacienteId: number;
    medicoId: number;
    fechaHora: string;
    motivo?: string;
    estado?: CitaEstado;
    notas?: string;
}

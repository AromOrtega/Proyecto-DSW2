export interface Notificacion {
    id?: number;
    userId: number;
    mensaje: string;
    leida?: boolean;
    fechaCreacion?: string;
    tipo?: string;
}

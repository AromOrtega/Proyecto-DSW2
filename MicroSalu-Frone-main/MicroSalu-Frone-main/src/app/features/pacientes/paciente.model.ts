export interface Paciente {
  id?: number;
  nombre: string;
  apellido: string;
  numeroIdentificacion?: string; // Cambiado de email a numeroIdentificacion
  fechaNacimiento?: string;
  telefono?: string;
  direccion?: string;
  usuarioId?: number;            // ¡LA CLAVE! Cambiado de userId a usuarioId
  alergiaIds?: number[];         // Opcional: agregado por si luego usas las alergias
}

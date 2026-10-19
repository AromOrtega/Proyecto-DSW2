export interface Horario {
  id?: number;
  dia: string;        // ¡CORREGIDO! Antes decía diaSemana
  horaInicio: string;
  horaFin: string;
}

export interface Medico {
  id?: number;
  nombre: string;
  apellido: string;
  numeroLicencia?: string;
  telefono?: string;
  email?: string;
  especialidad: string;
  estado?: string;
  disponible?: boolean;
  tarifaConsulta?: number;
  horarios?: Horario[];
  usuario?: {
    id?: number;
    username?: string;
    password?: string;
  };
}

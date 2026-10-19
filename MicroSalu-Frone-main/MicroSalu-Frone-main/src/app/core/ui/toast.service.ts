import { Injectable, signal } from '@angular/core';

export interface ToastMessage {
    id: number;
    type: 'success' | 'error' | 'info' | 'warning';
    text: string;
}

let nextId = 1;

@Injectable({ providedIn: 'root' })
export class ToastService {
    readonly messages = signal<ToastMessage[]>([]);

    show(text: string, type: ToastMessage['type'] = 'info', duration = 4000): void {
        const id = nextId++;
        this.messages.update(list => [...list, { id, type, text }]);
        setTimeout(() => this.remove(id), duration);
    }

    success(text: string): void { this.show(text, 'success'); }
    error(text: string): void { this.show(text, 'error', 6000); }
    info(text: string): void { this.show(text, 'info'); }
    warning(text: string): void { this.show(text, 'warning'); }

    remove(id: number): void {
        this.messages.update(list => list.filter(m => m.id !== id));
    }
}

import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ConfirmService {
    readonly visible = signal(false);
    private _message = signal('¿Estás seguro?');
    readonly message = this._message.asReadonly();
    private resolver?: (value: boolean) => void;

    confirm(message: string): Promise<boolean> {
        this._message.set(message);
        this.visible.set(true);
        return new Promise<boolean>(resolve => { this.resolver = resolve; });
    }

    accept(): void {
        this.visible.set(false);
        this.resolver?.(true);
    }

    cancel(): void {
        this.visible.set(false);
        this.resolver?.(false);
    }
}

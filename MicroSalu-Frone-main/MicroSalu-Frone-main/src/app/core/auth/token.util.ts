export interface JwtPayload {
    sub: string;         // username
    roles: string[];
    userId?: number;
    exp: number;
    iat: number;
}

export function decodeToken(token: string): JwtPayload | null {
    try {
        const payload = token.split('.')[1];
        const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
        return JSON.parse(decoded) as JwtPayload;
    } catch {
        return null;
    }
}

export function isTokenExpired(token: string): boolean {
    const payload = decodeToken(token);
    if (!payload) return true;
    return Date.now() >= payload.exp * 1000;
}

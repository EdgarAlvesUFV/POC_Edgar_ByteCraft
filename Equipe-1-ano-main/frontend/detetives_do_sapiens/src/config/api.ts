export const API_BASE =
  (import.meta.env.VITE_API_URL as string)?.replace(/\/+$/, "");

export function getApiUrl(path: string) {
  return `${API_BASE}${path.startsWith("/") ? path : `/${path}`}`;
}

export function getAuthHeaders(token?: string) {
  return {
    "Content-Type": "application/json",
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };
}

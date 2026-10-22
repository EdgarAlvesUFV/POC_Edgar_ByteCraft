interface LoadingOverlayProps {
  show: boolean;
  message: string;
}

export function LoadingOverlay({ show, message }: LoadingOverlayProps) {
  if (!show) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-3xl p-8 max-w-sm w-full mx-4 shadow-xl">
        <div className="text-center">
          <div className="w-12 h-12 border-4 border-pill-yellow border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
          <p className="text-lg font-extrabold text-pill-text">{message}</p>
        </div>
      </div>
    </div>
  );
}
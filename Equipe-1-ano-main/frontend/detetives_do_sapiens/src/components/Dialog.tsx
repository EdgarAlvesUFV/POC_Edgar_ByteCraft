import { ReactNode } from "react";
import { X } from "lucide-react";
import { PillButton } from "./PillButton";

interface DialogProps {
  open: boolean;
  onClose: () => void;
  children: ReactNode;
  title?: string;
}

export function Dialog({ open, onClose, children, title }: DialogProps) {
  if (!open) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-3xl p-8 max-w-md w-full mx-4 shadow-xl">
        {title && (
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-xl font-extrabold text-pill-text">{title}</h2>
            <button
              onClick={onClose}
              className="text-pill-text hover:text-pill-text/70"
            >
              <X size={24} />
            </button>
          </div>
        )}
        {children}
      </div>
    </div>
  );
}

interface SuccessDialogProps {
  open: boolean;
  onContinue: () => void;
  message: string;
}

export function SuccessDialog({ open, onContinue, message }: SuccessDialogProps) {
  return (
    <Dialog open={open} onClose={onContinue}>
      <div className="text-center">
        <div className="mb-6">
          <div className="w-16 h-16 bg-green-500 rounded-full flex items-center justify-center mx-auto mb-4">
            <span className="text-white text-2xl font-bold">✓</span>
          </div>
          <h3 className="text-lg font-extrabold text-pill-text">{message}</h3>
        </div>
        <PillButton onClick={onContinue} size="large">
          CONTINUAR
        </PillButton>
      </div>
    </Dialog>
  );
}
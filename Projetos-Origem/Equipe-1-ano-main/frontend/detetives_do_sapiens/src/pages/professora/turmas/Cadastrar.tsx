import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { PillButton } from "@/components/PillButton";
import { LoadingOverlay } from "@/components/LoadingOverlay";
import { SuccessDialog } from "@/components/Dialog";
import { useCreateTurma } from "@/hooks/useTurmas";
import { useAuth } from "@/contexts/AuthContext";

interface FormData {
  nome: string;
  ano: string;
  turno: string;
}

interface FormErrors {
  nome?: string;
  ano?: string;
  turno?: string;
}

const CadastrarTurma = () => {
  const navigate = useNavigate();
  const { user } = useAuth(); // ✅ token já é usado dentro do hook, não precisa aqui

  const professorId = user?.id;
  const createTurmaMutation = useCreateTurma(professorId); // ✅ agora só 1 argumento

  const [formData, setFormData] = useState<FormData>({
    nome: "",
    ano: "",
    turno: "",
  });

  const [errors, setErrors] = useState<FormErrors>({});
  const [showSuccess, setShowSuccess] = useState(false);

  const validateForm = (): boolean => {
    const newErrors: FormErrors = {};

    if (!formData.nome.trim()) newErrors.nome = "Nome é obrigatório";
    else if (formData.nome.length < 2 || formData.nome.length > 80)
      newErrors.nome = "Nome deve ter entre 2 e 80 caracteres";

    const anoNum = parseInt(formData.ano, 10);
    if (!formData.ano || isNaN(anoNum)) newErrors.ano = "Ano é obrigatório";
    else if (anoNum < 1 || anoNum > 9)
      newErrors.ano = "Ano deve estar entre 1 e 9";

    if (!formData.turno) newErrors.turno = "Turno é obrigatório";

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (createTurmaMutation.isPending) return;
    if (!validateForm()) return;

    try {
      await createTurmaMutation.mutateAsync({
        nome: formData.nome.trim(),
        ano: parseInt(formData.ano, 10),
        turno: formData.turno.toUpperCase() as "MANHA" | "TARDE" | "NOITE",
      });

      setFormData({ nome: "", ano: "", turno: "" });
      setErrors({});
      setShowSuccess(true);
    } catch (error: any) {
      if (error?.type === "validation" && error.errors) {
        setErrors({
          nome: error.errors.nome,
          ano: error.errors.ano,
          turno: error.errors.turno,
        });
      }
    }
  };

  const handleSuccessContinue = () => {
    setShowSuccess(false);
    navigate("/professora/turmas");
  };

  const handleInputChange = (field: keyof FormData, value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    if (errors[field]) setErrors((prev) => ({ ...prev, [field]: undefined }));
  };

  return (
    <>
      <div
        className="min-h-screen bg-cover bg-center bg-no-repeat flex flex-col items-center justify-center p-8"
        style={{ backgroundImage: "url('/assets/background_image.png')" }}
      >
        <div className="bg-content-bg border-2 border-content-border rounded-3xl p-8 w-full max-w-md">
          <div className="mb-8 text-center">
            <div className="bg-pill-yellow text-pill-text border-2 border-pill-border rounded-full px-6 py-3 font-extrabold shadow-[2px_4px_0px_rgba(0,0,0,0.35)] inline-block">
              CADASTRANDO TURMA
            </div>
          </div>

          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Nome */}
            <div>
              <label className="block text-pill-text font-bold mb-2">
                TURMA:
              </label>
              <input
                type="text"
                value={formData.nome}
                onChange={(e) => handleInputChange("nome", e.target.value)}
                placeholder="EXEMPLO: TURMA 201"
                className="w-full bg-pill-yellow text-pill-text border-2 border-pill-border rounded-full px-4 py-3 font-semibold placeholder-pill-text/60 focus:outline-none focus:ring-2 focus:ring-pill-border focus:ring-offset-2"
              />
              {errors.nome && (
                <p className="text-red-600 text-sm mt-1 font-semibold">
                  {errors.nome}
                </p>
              )}
            </div>

            {/* Ano */}
            <div>
              <label className="block text-pill-text font-bold mb-2">
                ANO:
              </label>
              <input
                type="number"
                min="1"
                max="9"
                value={formData.ano}
                onChange={(e) => handleInputChange("ano", e.target.value)}
                placeholder="EXEMPLO: 1º"
                className="w-full bg-pill-yellow text-pill-text border-2 border-pill-border rounded-full px-4 py-3 font-semibold placeholder-pill-text/60 focus:outline-none focus:ring-2 focus:ring-pill-border focus:ring-offset-2"
              />
              {errors.ano && (
                <p className="text-red-600 text-sm mt-1 font-semibold">
                  {errors.ano}
                </p>
              )}
            </div>

            {/* Turno */}
            <div>
              <label className="block text-pill-text font-bold mb-2">
                TURNO:
              </label>
              <select
                value={formData.turno}
                onChange={(e) => handleInputChange("turno", e.target.value)}
                className="w-full bg-pill-yellow text-pill-text border-2 border-pill-border rounded-full px-4 py-3 font-semibold focus:outline-none focus:ring-2 focus:ring-pill-border focus:ring-offset-2"
              >
                <option value="">EXEMPLO: TURNO MATUTINO</option>
                <option value="MANHA">MANHÃ</option>
                <option value="TARDE">TARDE</option>
                <option value="NOITE">NOITE</option>
              </select>
              {errors.turno && (
                <p className="text-red-600 text-sm mt-1 font-semibold">
                  {errors.turno}
                </p>
              )}
            </div>

            {/* Botões */}
            <div className="flex gap-4 pt-4">
              <PillButton
                type="submit"
                disabled={createTurmaMutation.isPending}
                className="flex-1"
              >
                {createTurmaMutation.isPending ? "SALVANDO..." : "CONFIRMAR"}
              </PillButton>

              <PillButton
                type="button"
                variant="secondary"
                onClick={() => navigate("/professora/turmas")}
                disabled={createTurmaMutation.isPending}
                className="flex-1"
              >
                CANCELAR
              </PillButton>
            </div>
          </form>
        </div>
      </div>

      <LoadingOverlay
        show={createTurmaMutation.isPending}
        message="CADASTRANDO TURMA"
      />

      <SuccessDialog
        open={showSuccess}
        onContinue={handleSuccessContinue}
        message="TURMA CADASTRADA COM SUCESSO!"
      />
    </>
  );
};

export default CadastrarTurma;

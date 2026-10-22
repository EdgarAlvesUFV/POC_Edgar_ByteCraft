import { useNavigate } from "react-router-dom";
import { useAuth } from "@/contexts/AuthContext";
import { PillButton } from "@/components/PillButton";

const ProfessoraDashboard = () => {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  return (
    <div
      className="min-h-screen bg-cover bg-center bg-no-repeat flex flex-col items-center justify-center p-8"
      style={{ backgroundImage: "url('/assets/background_image.png')" }}
    >
      <div className="flex flex-col gap-6 items-center">
        <PillButton
          size="large"
          onClick={() => navigate(`/professora/turmas?professorId=${user?.id}`)}
        >
          GERENCIAR TURMAS
        </PillButton>

        <PillButton
          size="large"
          onClick={() => navigate(`/professora/acompanhar-desempenho`)}
        >
          ACOMPANHAR DESEMPENHO
        </PillButton>
      </div>

      <div className="absolute bottom-8 right-8">
        <PillButton variant="secondary" onClick={() => logout()}>
          SAIR
        </PillButton>
      </div>
    </div>
  );
};

export default ProfessoraDashboard;

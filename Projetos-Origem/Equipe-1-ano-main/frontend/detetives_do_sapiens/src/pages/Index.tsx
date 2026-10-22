import { useNavigate } from "react-router-dom";
import { PillButton } from "@/components/PillButton";

const Index = () => {
  const navigate = useNavigate();

  return (
    <div className="relative min-h-screen flex flex-col items-center justify-center p-8 overflow-hidden">
      {/* Vídeo de fundo */}
      <video
        autoPlay
        loop
        muted
        playsInline
        className="absolute inset-0 w-full h-full object-cover -z-10"
      >
        <source src="/assets/video_index.mp4" type="video/mp4" />
        Seu navegador não suporta vídeos em HTML5.
      </video>

      {/* Botão PROFESSORA no topo direito */}
      <div className="absolute top-8 right-8">
        <PillButton onClick={() => navigate("/professora/login")}>
          PROFESSORA
        </PillButton>
      </div>

      {/* Botão INICIAR centralizado */}
      <div className="flex items-center justify-center mt-40">
        <PillButton
          size="large"
          onClick={() => navigate("/selecionar-turma")}
        >
          INICIAR
        </PillButton>
      </div>
    </div>
  );
};

export default Index;

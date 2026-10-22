import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "@/contexts/AuthContext";
import { PillButton } from "@/components/PillButton";
import {
  useListTurmas,
  useDeleteTurma,
  useCreateTurma,
  useUpdateTurma,
} from "@/hooks/useTurmas";
import { useQueryClient } from "@tanstack/react-query";

type Draft = { nome: string; ano: string; turno: "MANHA" | "TARDE" | "NOITE" | "" };

const Field = ({ label, children }: { label: string; children: React.ReactNode }) => (
  <div>
    <label className="block text-pill-text font-bold mb-2">{label}</label>
    {children}
  </div>
);

const CreateModal = ({
  open,
  onClose,
  onSubmit,
  draft,
  setDraft,
  pending,
}: {
  open: boolean;
  onClose: () => void;
  onSubmit: (e: React.FormEvent<HTMLFormElement>) => void;
  draft: Draft;
  setDraft: React.Dispatch<React.SetStateAction<Draft>>;
  pending: boolean;
}) =>
  !open ? null : (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
      <div className="w-full max-w-md rounded-3xl border-2 border-content-border bg-content-bg p-6">
        <div className="mb-6 text-center">
          <div className="bg-pill-yellow text-pill-text border-2 border-pill-border rounded-full px-6 py-3 font-extrabold shadow-[2px_4px_0px_rgba(0,0,0,0.35)] inline-block">
            CADASTRANDO TURMA
          </div>
        </div>

        <form onSubmit={onSubmit} className="space-y-5">
          <Field label="TURMA:">
            <input
              type="text"
              value={draft.nome}
              onChange={(e) => setDraft((d) => ({ ...d, nome: e.target.value }))}
              className="w-full bg-pill-yellow text-pill-text border-2 border-pill-border rounded-full px-4 py-3 font-semibold focus:outline-none focus:ring-2 focus:ring-pill-border focus:ring-offset-2"
            />
          </Field>

          <Field label="ANO:">
            <input
              type="number"
              min={1}
              max={9}
              value={draft.ano}
              onChange={(e) => setDraft((d) => ({ ...d, ano: e.target.value }))}
              className="w-full bg-pill-yellow text-pill-text border-2 border-pill-border rounded-full px-4 py-3 font-semibold focus:outline-none focus:ring-2 focus:ring-pill-border focus:ring-offset-2"
            />
          </Field>

          <Field label="TURNO:">
            <select
              value={draft.turno}
              onChange={(e) =>
                setDraft((d) => ({ ...d, turno: e.target.value as Draft["turno"] }))
              }
              className="w-full bg-pill-yellow text-pill-text border-2 border-pill-border rounded-full px-4 py-3 font-semibold focus:outline-none focus:ring-2 focus:ring-pill-border focus:ring-offset-2"
            >
              <option value="">Selecione</option>
              <option value="MANHA">MANHÃ</option>
              <option value="TARDE">TARDE</option>
              <option value="NOITE">NOITE</option>
            </select>
          </Field>

          <div className="flex gap-3 pt-2">
            <PillButton type="submit" disabled={pending}>
              {pending ? "SALVANDO..." : "CONFIRMAR"}
            </PillButton>
            <PillButton type="button" variant="secondary" onClick={onClose} disabled={pending}>
              CANCELAR
            </PillButton>
          </div>
        </form>
      </div>
    </div>
  );

const EditModal = ({
  open,
  onClose,
  onSubmit,
  draft,
  setDraft,
  pending,
}: {
  open: boolean;
  onClose: () => void;
  onSubmit: (e: React.FormEvent<HTMLFormElement>) => void;
  draft: Draft;
  setDraft: React.Dispatch<React.SetStateAction<Draft>>;
  pending: boolean;
}) =>
  !open ? null : (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
      <div className="w-full max-w-md rounded-3xl border-2 border-content-border bg-content-bg p-6">
        <div className="mb-6 text-center">
          <div className="bg-pill-yellow text-pill-text border-2 border-pill-border rounded-full px-6 py-3 font-extrabold shadow-[2px_4px_0px_rgba(0,0,0,0.35)] inline-block">
            EDITAR TURMA
          </div>
        </div>

        <form onSubmit={onSubmit} className="space-y-5">
          <Field label="TURMA:">
            <input
              type="text"
              value={draft.nome}
              onChange={(e) => setDraft((d) => ({ ...d, nome: e.target.value }))}
              className="w-full bg-pill-yellow text-pill-text border-2 border-pill-border rounded-full px-4 py-3 font-semibold focus:outline-none focus:ring-2 focus:ring-pill-border focus:ring-offset-2"
            />
          </Field>

          <Field label="ANO:">
            <input
              type="number"
              min={1}
              max={9}
              value={draft.ano}
              onChange={(e) => setDraft((d) => ({ ...d, ano: e.target.value }))}
              className="w-full bg-pill-yellow text-pill-text border-2 border-pill-border rounded-full px-4 py-3 font-semibold focus:outline-none focus:ring-2 focus:ring-pill-border focus:ring-offset-2"
            />
          </Field>

          <Field label="TURNO:">
            <select
              value={draft.turno}
              onChange={(e) =>
                setDraft((d) => ({ ...d, turno: e.target.value as Draft["turno"] }))
              }
              className="w-full bg-pill-yellow text-pill-text border-2 border-pill-border rounded-full px-4 py-3 font-semibold focus:outline-none focus:ring-2 focus:ring-pill-border focus:ring-offset-2"
            >
              <option value="">Selecione</option>
              <option value="MANHA">MANHÃ</option>
              <option value="TARDE">TARDE</option>
              <option value="NOITE">NOITE</option>
            </select>
          </Field>

          <div className="flex gap-3 pt-2">
            <PillButton type="submit" disabled={pending}>
              {pending ? "SALVANDO..." : "SALVAR"}
            </PillButton>
            <PillButton type="button" variant="secondary" onClick={onClose} disabled={pending}>
              CANCELAR
            </PillButton>
          </div>
        </form>
      </div>
    </div>
  );
const TurmasList = () => {
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();
  const professorId = user?.id 

  const queryClient = useQueryClient();
  const {
    data: turmas,
    isLoading,
    error,
    refetch, 
  } = useListTurmas();
  const deleteMutation = useDeleteTurma(professorId);
  const createMutation = useCreateTurma(professorId);
  const updateMutation = useUpdateTurma(professorId);

  useEffect(() => {
    if (createMutation.isSuccess || updateMutation.isSuccess || deleteMutation.isSuccess) {
      queryClient.invalidateQueries({ queryKey: ["turmas"] });
      refetch();
    }
  }, [
    createMutation.isSuccess,
    updateMutation.isSuccess,
    deleteMutation.isSuccess,
    queryClient,
    refetch,
  ]);

  const [showCreate, setShowCreate] = useState(false);
  const [showEdit, setShowEdit] = useState(false);
  const [createDraft, setCreateDraft] = useState<Draft>({ nome: "", ano: "", turno: "" });
  const [editDraft, setEditDraft] = useState<Draft>({ nome: "", ano: "", turno: "" });
  const [editingId, setEditingId] = useState<number | null>(null);

  const getTurnoDisplay = (t: string) =>
    t === "MANHA" ? "MANHÃ" : t === "TARDE" ? "TARDE" : t === "NOITE" ? "NOITE" : t;

  const openCreate = () => {
    setCreateDraft({ nome: "", ano: "", turno: "" });
    setShowCreate(true);
  };

  const openEdit = (t: { id?: number; nome: string; ano: number; turno: any }) => {
    if (!t.id) return;
    setEditingId(t.id);
    setEditDraft({ nome: t.nome, ano: String(t.ano), turno: t.turno });
    setShowEdit(true);
  };

  const onCreateSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const payload = {
      nome: createDraft.nome.trim(),
      ano: parseInt(createDraft.ano, 10),
      turno: createDraft.turno as "MANHA" | "TARDE" | "NOITE",
    };
    if (!payload.nome || Number.isNaN(payload.ano) || !payload.turno) return;
    await createMutation.mutateAsync(payload).catch(() => {});
    setShowCreate(false);
  };

  const onEditSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!editingId) return;

    const payload = {
      nome: editDraft.nome.trim(),
      ano: parseInt(editDraft.ano, 10),
      turno: editDraft.turno as "MANHA" | "TARDE" | "NOITE",
    };
    if (!payload.nome || Number.isNaN(payload.ano) || !payload.turno) return;
    await updateMutation.mutateAsync({ id: editingId, data: payload }).catch(() => {});
    setShowEdit(false);
  };

  return (
    <div
      className="min-h-screen bg-cover bg-center bg-no-repeat flex flex-col items-center p-8"
      style={{ backgroundImage: "url('/assets/background_image.png')" }}
    >
      <div className="bg-content-bg border-2 border-content-border rounded-3xl p-6 w-full max-w-3xl shadow-lg">
        {/* Cabeçalho com botão VOLTAR */}
        <div className="mb-6 flex justify-between items-center">
          <div className="bg-pill-yellow text-pill-text border-2 border-pill-border rounded-full px-6 py-3 font-extrabold shadow-[2px_4px_0px_rgba(0,0,0,0.35)]">
            LISTA DE TURMAS
          </div>
          <PillButton variant="secondary" onClick={() => navigate(-1)}>
            ← VOLTAR
          </PillButton>
        </div>

        {isLoading && <p className="text-pill-text text-center font-semibold">Carregando turmas...</p>}
        {error && <p className="text-red-500 text-center font-semibold">Erro ao carregar turmas.</p>}

        {!isLoading && !error && turmas && turmas.length > 0 && (
          <table className="w-full border-collapse text-pill-text">
            <thead>
              <tr className="border-b-2 border-pill-border">
                <th className="p-3 text-left">Nome</th>
                <th className="p-3 text-left">Ano</th>
                <th className="p-3 text-left">Turno</th>
                <th className="p-3 text-right">Ações</th>
              </tr>
            </thead>
            <tbody>
              {turmas.map((t) => (
                <tr key={t.id} className="border-b border-pill-border">
                  <td className="p-3">{t.nome}</td>
                  <td className="p-3">{t.ano}</td>
                  <td className="p-3">{getTurnoDisplay(t.turno)}</td>
                  <td className="p-3 text-right flex gap-2 justify-end">
                    <PillButton variant="secondary" onClick={() => openEdit(t)}>
                      EDITAR
                    </PillButton>
                    <PillButton variant="primary" onClick={() => deleteMutation.mutate(t.id)}>
                      EXCLUIR
                    </PillButton>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}

        {!isLoading && turmas && turmas.length === 0 && (
          <p className="text-pill-text text-center font-semibold mt-4">
            Nenhuma turma cadastrada ainda.
          </p>
        )}

        <div className="flex justify-center mt-6">
          <PillButton onClick={openCreate}>+ NOVA TURMA</PillButton>
        </div>
      </div>

      <CreateModal
        open={showCreate}
        onClose={() => setShowCreate(false)}
        onSubmit={onCreateSubmit}
        draft={createDraft}
        setDraft={setCreateDraft}
        pending={createMutation.isPending}
      />

      <EditModal
        open={showEdit}
        onClose={() => setShowEdit(false)}
        onSubmit={onEditSubmit}
        draft={editDraft}
        setDraft={setEditDraft}
        pending={updateMutation.isPending}
      />
    </div>
  );
};

export default TurmasList;

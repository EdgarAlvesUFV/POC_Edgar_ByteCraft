package app.model;

import java.util.List;

public class Sessao {
        private long IdSessao;
        private Turma turma;
        private List<Misterio> misterios;
        private StatusSessao statusSessao;

        //construtor padrao - vazio
        public Sessao() {
        }

        //construtor que recebe Turma e status da sessao
        public Sessao(Turma turma){
            this.turma = turma;
            this.statusSessao = StatusSessao.ATIVA;
        }

    //Getters
        public long getIdSessao() {
            return IdSessao;
        }

        public Turma getTurma() {
            return turma;
        }

        public List<Misterio> getMisterios() {
            return misterios;
        }

        public StatusSessao getStatusSessao() { return statusSessao; }
        //Setters
        public void setIdSessao(long idSessao) {
            IdSessao = idSessao;
        }

        public void setTurma(Turma turma) {
            this.turma = turma;
        }

        public void setMisterios(List<Misterio> misterios) {
            this.misterios = misterios;
        }

        public void setStatusSessao(StatusSessao statusSessao) { this.statusSessao = statusSessao; }
    }


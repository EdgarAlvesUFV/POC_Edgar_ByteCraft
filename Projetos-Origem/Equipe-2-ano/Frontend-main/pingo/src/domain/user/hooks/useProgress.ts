"use client";

import { useState, useEffect } from "react";
import { getUserProgress, saveProgress } from "../service/userProgressService";

export const useProgress = () => {
  const [nickname, setNickname] = useState('');
  const [progress, setProgress] = useState<number>(0);
  const [userId, setUserId] = useState<number>(0);

  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  useEffect(() => {
    const fetchStages = async () => {
      try {
        const response = await getUserProgress(localStorage.getItem('nickname'));
        setProgress(response.currentStage);
        setUserId(response.userId);
        setNickname(response.nickname);
      } catch (err) {
        setErrorMessage("Não foi possível obter o progresso do usuário.");
        console.error(err);
      }
    };

    fetchStages();
  }, []); 

  async function updateProgress(currentStage: number) {
    try {
      const data = await saveProgress({ nickname: nickname, currentStage: currentStage});
      setProgress(data.currentStage);
    } catch (err) {
      console.error("Erro ao atualizar progresso:", err);
    }
  }
  return {
    userId,
    nickname,
    progress,
    updateProgress
  };
};

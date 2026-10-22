"use client";

import { useState, useEffect } from "react";
import { listStages } from "../service/stageService";
import { StageResponse } from "../types";

export const useStage = () => {
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [stages, setStages] = useState<StageResponse[]>([]);

  useEffect(() => {
    const fetchStages = async () => {
      try {
        const response = await listStages();
        setStages(response); 
      } catch (err) {
        setErrorMessage("Não foi possível carregar as fases.");
        console.error(err);
      }
    };

    fetchStages();
  }, []); 

  return {
    stages
  };
};

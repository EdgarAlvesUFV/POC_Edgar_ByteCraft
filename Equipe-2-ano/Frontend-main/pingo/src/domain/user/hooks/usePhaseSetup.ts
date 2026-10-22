"use client";

import { useState, useEffect } from "react";
import { NetworkDeviceResponse } from "../types";
import { getPhaseSetup } from "../service/phaseSetupService";

export const usePhaseSetup = () => {
  const [devices, setDevices] = useState<NetworkDeviceResponse[]>();
  const [correctPaths, setCorrectPaths] = useState<number[][][]>();
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  useEffect(() => {
    const getSetup = async () => {
      try {
        const response = await getPhaseSetup();
        setCorrectPaths(response.correctPaths);
        setDevices(response.devices)
      } catch (err) {
        setErrorMessage("Não foi possível obter o progresso do usuário.");
        console.error(err);
      }
    };

    getSetup();
  }, []);
  
  return {
    devices,
    correctPaths
  };
};

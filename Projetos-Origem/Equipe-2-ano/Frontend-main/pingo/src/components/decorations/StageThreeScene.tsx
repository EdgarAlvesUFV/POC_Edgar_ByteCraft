"use client";
import React, { useEffect, useRef, useState, useCallback } from "react";
import Image from "next/image";
import NetworkDevice from "../ui/NetworkDevice";
import { ButtonSend } from "../ui/ButtonSend";
import { motion } from "framer-motion";
import { StageState } from "@/app/stage-3/page";
import { NetworkDeviceResponse } from "@/domain/user/types";

interface StageThreeSceneProps {
  className?: string;
  devices: NetworkDeviceResponse[];
  correctPaths: number[][][];
  state: StageState;
  isPaused: boolean;
  onNotConnected?: (action: string) => void;
  onClickDistance?: (action: string) => void;
  onFirstNotModem?: (action: string) => void;
  onEndNotModem?: (action: string) => void;
  notSwitch?: (action: string) => void;
  onDeffect?: (action: string) => void;
  onBackPlaying?: (action: string) => void;
  onSubmit?: (action: string) => void;
  onComplete?: (action: string) => void;
  onNotPaused?: (action: string) => void;
}

const imagePaths = {
  details: "/image/stage-3/details-background.svg",
};

interface Position {
  hasDeffect?: boolean;
  name: string;
  num: number;
  positionX: number;
  positionY: number;
}

type Connection = [Position, Position];

export default function StageThreeScene({
  devices,
  correctPaths,
  state,
  isPaused,
  onNotConnected,
  onClickDistance,
  onFirstNotModem,
  onEndNotModem,
  notSwitch,
  onDeffect,
  onBackPlaying,
  onSubmit,
  onComplete
}: StageThreeSceneProps) {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const refs = useRef<(HTMLDivElement | null)[]>([]);
  const canvasRectRef = useRef<DOMRect | null>(null);
  const ctxRef = useRef<CanvasRenderingContext2D | null>(null);
  const isPausedRef = useRef(isPaused);
  const [connections, setConnections] = useState<Connection[]>([]);
  const clickRef = useRef<Position[]>([
    { name: "", num: -1, positionX: -1, positionY: -1 },
    { name: "", num: -1, positionX: -1, positionY: -1 },
  ]);
  const [target, setTarget] = useState({ x: 0, y: 0 });
  const atualDevice = useRef<Position>(null);
  const isFinalPath = useRef<number>(0);
  const most = useRef<number>(0);
  const maxPath = useRef<Connection[]>([]);
  const modemConnection = useRef<Connection | undefined>(undefined);
  let switchPass = false;
  let aux = 0;
  let currentPath: Connection[] = [];
  let destinyDevice: Position;
  let pathSelected = 0;
  const delay = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));

  const networkDevicesData = devices.map((device, index) => ({
    ...device,
    num: index + 1,
  }));

  async function verifyPause() {
    if(!isPaused) {
      if(atualDevice.current == null) return;
      if(atualDevice.current.num == 0 ) {
        return
      } else if(isFinalPath.current == 1) {
        await animateMessage(atualDevice.current.num, 0);
        atualDevice.current = modemConnection.current![0];
        await correctPath();
        isFinalPath.current = 0;
        return;
      } else if(isFinalPath.current == 2) {
        await correctPath();
        return;
      }
      await animateMessage(atualDevice.current.num, 0);
      if (onBackPlaying) onBackPlaying("BACK_TO_PLAYING");
    }
  }
  useEffect(() => {
    verifyPause();
  }, [isPaused]);

  useEffect(() => {
    if (state != StageState.SUBMITING) {
      const canvasRect = canvasRectRef.current;
      if (!canvasRect) return;
      const divRect1 = refs.current[0]!.getBoundingClientRect();
      const initX = divRect1.left - canvasRect.left + divRect1.width / 2;
      const initY = divRect1.top - canvasRect.top + divRect1.height / 2;
      setTarget({ x: initX, y: initY });
    }
  }, [state]);

  const clearCanvas = useCallback(() => {
    if (!canvasRef.current || !ctxRef.current) return;
    const canvas = canvasRef.current;
    const ctx = ctxRef.current;
    ctx.clearRect(0, 0, canvas.width, canvas.height);
  }, []);

  const drawArrow = useCallback(
    (x1: number, y1: number, x2: number, y2: number, arrowSize: number) => {
      if (!ctxRef.current) return;
      const ctx = ctxRef.current;
      const angle = Math.atan2(y2 - y1, x2 - x1);
      ctx.save();
      ctx.translate(x2, y2);
      ctx.rotate(angle);
      ctx.fillStyle = "#00CFFF";
      ctx.beginPath();
      ctx.moveTo(0, 0);
      ctx.lineTo(-arrowSize, -arrowSize / 2);
      ctx.lineTo(-arrowSize, arrowSize / 2);
      ctx.closePath();
      ctx.fill();
      ctx.restore();
    },
    []
  );

  const drawLine = useCallback(
    (initX: number, initY: number, destinyX: number, destinyY: number) => {
      if (!ctxRef.current) return;
      const ctx = ctxRef.current;
      ctx.beginPath();
      ctx.moveTo(initX, initY);
      ctx.lineTo(destinyX, destinyY);
      ctx.stroke();
    },
    []
  );

  const drawConnection = useCallback(
    (
      pos1: Position,
      pos2: Position,
      lineWidth: number,
      arrowSize: number,
      dashArray: number[]
    ) => {
      if (
        !refs.current[pos1.num] ||
        !refs.current[pos2.num] ||
        !canvasRectRef.current ||
        !ctxRef.current
      )
        return;
      const canvasRect = canvasRectRef.current;
      const ctx = ctxRef.current;
      const divRect1 = refs.current[pos1.num]!.getBoundingClientRect();
      const divRect2 = refs.current[pos2.num]!.getBoundingClientRect();
      ctx.strokeStyle = "#00CFFF";
      ctx.lineWidth = lineWidth;
      ctx.setLineDash(dashArray);

      let initX = 0;
      let initY = 0;
      let destinyX = 0;
      let destinyY = 0;

      if (pos1.num === 0) {
        initX = divRect1.left - canvasRect.left + divRect1.width / 2;
        initY = divRect1.top - canvasRect.top;
        destinyX = initX;
        destinyY = divRect2.top - canvasRect.top + divRect2.height / 2;
        drawLine(initX, initY, destinyX, destinyY);
        initX = destinyX;
        initY = destinyY;
        destinyX = divRect2.left - canvasRect.left;
        destinyY = initY;
        drawLine(initX, initY, destinyX, destinyY);
        drawArrow(initX, initY, destinyX, destinyY, arrowSize);
      } else if (pos2.num === 10) {
        initX = divRect1.right - canvasRect.left;
        initY = divRect1.top - canvasRect.top + divRect1.height / 2;
        destinyX = divRect2.left - canvasRect.left + divRect2.width / 2;
        destinyY = initY;
        drawLine(initX, initY, destinyX, destinyY);
        initX = destinyX;
        initY = destinyY;
        destinyX = initX;
        destinyY = divRect2.bottom - canvasRect.top;
        drawLine(initX, initY, destinyX, destinyY);
        drawArrow(initX, initY, destinyX, destinyY, arrowSize);
      } else if (pos1.positionY === pos2.positionY) {
        if (pos1.positionX < pos2.positionX) {
          initX = divRect1.left - canvasRect.left + divRect1.width / 2;
          initY = divRect1.bottom - canvasRect.top;
          destinyX = divRect2.left - canvasRect.left + divRect2.width / 2;
          destinyY = divRect2.top - canvasRect.top;
          drawLine(initX, initY, destinyX, destinyY);
        } else {
          initX = divRect1.left - canvasRect.left + divRect1.width / 2;
          initY = divRect1.top - canvasRect.top;
          destinyX = divRect2.left - canvasRect.left + divRect2.width / 2;
          destinyY = divRect2.bottom - canvasRect.top;
          drawLine(initX, initY, destinyX, destinyY);
        }
      } else {
        initX = divRect1.right - canvasRect.left;
        initY = divRect1.top - canvasRect.top + divRect2.height / 2;
        destinyX = divRect2.left - canvasRect.left;
        destinyY = divRect2.top - canvasRect.top + divRect2.height / 2;
        drawLine(initX, initY, destinyX, destinyY);
        drawArrow(initX, initY, destinyX, destinyY, arrowSize);
      }
    },
    [drawArrow, drawLine]
  );

  const redrawConnections = useCallback(() => {
    clearCanvas();
    const canvas = canvasRef.current;
    if (!canvas) return;
    const newRect = canvas.parentElement!.getBoundingClientRect();
    canvasRectRef.current = newRect;
    const dpr = window.devicePixelRatio || 1;
    canvas.width = newRect.width * dpr;
    canvas.height = newRect.height * dpr;
    ctxRef.current!.scale(dpr, dpr);
    const baseWidth = 1400;
    const currentWidth = newRect.width;
    const scaleFactor = currentWidth / baseWidth;
    const baseLineWidth = 2;
    const baseArrowSize = 13;
    const baseDashLength = 10;
    const baseDashGap = 5;
    const responsiveLineWidth = Math.max(1, baseLineWidth * scaleFactor);
    const responsiveArrowSize = Math.max(5, baseArrowSize * scaleFactor);
    const responsiveDashArray = [
      baseDashLength * scaleFactor,
      baseDashGap * scaleFactor,
    ];
    connections.forEach(([pos1, pos2]) => {
      drawConnection(
        pos1,
        pos2,
        responsiveLineWidth,
        responsiveArrowSize,
        responsiveDashArray
      );
    });
  }, [clearCanvas, connections, drawConnection]);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const dpr = window.devicePixelRatio || 1;
    const rect = canvas.parentElement!.getBoundingClientRect();
    canvasRectRef.current = rect;
    canvas.width = rect.width * dpr;
    canvas.height = rect.height * dpr;
    ctxRef.current = canvas.getContext("2d");
    if (!ctxRef.current) return;
    ctxRef.current.scale(dpr, dpr);
    ctxRef.current.strokeStyle = "#00CFFF";
    ctxRef.current.lineWidth = 2;
    ctxRef.current.setLineDash([10, 5]);
    const handleResize = () => {
      redrawConnections();
    };
    window.addEventListener("resize", handleResize);
    redrawConnections();
    return () => {
      window.removeEventListener("resize", handleResize);
    };
  }, [redrawConnections]);

  useEffect(() => {
    redrawConnections();
  }, [connections, redrawConnections]);

  const handleClick = (e: React.MouseEvent, pos: Position) => {
    if (state != StageState.PLAYING) return;
    const currentClick = clickRef.current;
    if (currentClick[0].num === -1) {
      currentClick[0] = pos;
    } else if (currentClick[1].num === -1 && pos.num !== currentClick[0].num) {
      currentClick[1] = pos;
      const connectionExists = connections.find(
        ([pos1, pos2]) =>
          (pos1.num === currentClick[0].num &&
            pos2.num === currentClick[1].num) ||
          (pos1.num === currentClick[1].num && pos2.num === currentClick[0].num)
      );
      if (!connectionExists) {
        let pos1 = currentClick[0];
        let pos2 = currentClick[1];

        if (
          pos1.num !== 0 &&
          pos2.num !== 10 &&
          pos1.num !== 10 &&
          pos2.num !== 0
        ) {
          if (pos1.positionY > pos2.positionY) {
            [pos1, pos2] = [pos2, pos1];
          }
        } else if (pos1.num === 10 || pos2.num === 0) {
          [pos1, pos2] = [pos2, pos1];
        }

        if (
          (Math.abs(pos1.positionY - pos2.positionY) > 1 ||
            (pos2.num == 10 && pos1.positionY <= 1)) &&
          !(pos2.num == 10 && pos1.positionY == 2)
        ) {
          if (onClickDistance) onClickDistance("CLICK_DISTANCE");
        } else if (
          Math.abs(pos1.positionX - pos2.positionX) == 2 &&
          pos1.positionY == pos2.positionY
        ) {
          if (pos1.num > pos2.num) {
            setConnections((prev) => [
              ...prev,
              [
                pos1,
                networkDevicesData.find(
                  (device) => device.num == pos1.num - 1
                )!,
              ],
            ]);
            setConnections((prev) => [
              ...prev,
              [
                pos2,
                networkDevicesData.find(
                  (device) => device.num === pos2.num + 1
                )!,
              ],
            ]);
          } else {
            setConnections((prev) => [
              ...prev,
              [
                pos2,
                networkDevicesData.find(
                  (device) => device.num == pos2.num - 1
                )!,
              ],
            ]);
            setConnections((prev) => [
              ...prev,
              [
                pos1,
                networkDevicesData.find(
                  (device) => device.num == pos1.num + 1
                )!,
              ],
            ]);
          }
        } else {
          setConnections((prev) => [...prev, [pos1, pos2]]);
        }
      }

      currentClick[0] = { name: "", num: -1, positionX: -1, positionY: -1 };
      currentClick[1] = { name: "", num: -1, positionX: -1, positionY: -1 };
    }
  };

  const verifyPath = (path: number[][]): [number, Connection[]] => {
    let i = 0;
    const maxPathAxtual: Connection[] = [];
    while (i < path.length - 1) {
      const connection = connections.find(
        ([pos1, pos2]) =>
          (pos1.positionX === path[i][0] &&
            pos1.positionY === path[i][1] &&
            pos2.positionX == path[i + 1][0] &&
            pos2.positionY == path[i + 1][1]) ||
          (pos2.positionX === path[i][0] &&
            pos2.positionY === path[i][1] &&
            pos1.positionX == path[i + 1][0] &&
            pos1.positionY == path[i + 1][1])
      );
      if (!connection) {
        break;
      }
      maxPathAxtual.push(connection);
      i++;
    }
    return [i, maxPathAxtual];
  };

  const animateMessage = async (disp1: number, disp2: number) => {
    while (isPausedRef.current) {
      await delay(50);
    }
    if (!canvasRectRef.current) return;
    const canvasRect = canvasRectRef.current;
    const divRect1 = refs.current[disp1]!.getBoundingClientRect();
    const divRect2 = refs.current[disp2]!.getBoundingClientRect();
    let destinyX: number;
    let destinyY: number;

    if (disp1 === 0) {
      destinyX = divRect1.left - canvasRect.left + divRect1.width / 2;
      destinyY = divRect2.top - canvasRect.top + divRect2.height / 2;
      setTarget({ x: destinyX, y: destinyY });
      await delay(500);
      destinyX = divRect2.left - canvasRect.left + divRect2.width / 2;
      setTarget({ x: destinyX, y: destinyY });
      await delay(200);
    } else if (disp2 === 10) {
      await delay(500);
      destinyX = divRect2.left - canvasRect.left + divRect2.width / 2;
      destinyY = divRect1.top - canvasRect.top + divRect1.height / 2;
      setTarget({ x: destinyX, y: destinyY });
      await delay(800);
      destinyY = divRect2.top - canvasRect.top + divRect2.height / 2;
      setTarget({ x: destinyX, y: destinyY });
      await delay(500);
    } else {
      await delay(400);
      destinyX = divRect2.left - canvasRect.left + divRect2.width / 2;
      destinyY = divRect2.top - canvasRect.top + divRect2.height / 2;
      setTarget({ x: destinyX, y: destinyY });
      await delay(400);
    }
  };

  const handleMessageAnimation = async () => {
    most.current = 0;
    for (let i = 0; i < correctPaths.length; i++) {
      [aux, currentPath] = verifyPath(correctPaths[i]);

      if (aux > most.current) {
        most.current = aux;
        maxPath.current = currentPath;
        pathSelected = i;
        if (most.current === correctPaths[pathSelected].length - 1) break;
      }
    }

    modemConnection.current = connections.find(
      ([pos1, pos2]) =>
        pos1.num === 0 &&
        pos2.positionY === 0 &&
        pos2.name === "modem" &&
        pos2.hasDeffect == false
    );

    if (modemConnection.current) {
      await animateMessage(modemConnection.current[0].num, modemConnection.current[1].num);
      atualDevice.current = modemConnection.current[1];
    } else {
      modemConnection.current = connections.find(
        ([pos1, pos2]) => pos1.num === 0 && pos2.positionY === 0
      );
      if (!modemConnection.current) {
        if (onBackPlaying) onBackPlaying("BACK_TO_PLAYING");
        return;
      }
      atualDevice.current = modemConnection.current[1];
      await animateMessage(modemConnection.current[0].num, modemConnection.current[1].num);
      if (onFirstNotModem) onFirstNotModem("FIRST_NOT_MODEM");
      return;
    }

    if (most.current < correctPaths[pathSelected].length - 1) {
      [aux, currentPath] = verifyPath(correctPaths[pathSelected]);
      maxPath.current = currentPath;
      most.current = aux;
      for (let j = 0; j < most.current; j++) {
        if (atualDevice.current.num == maxPath.current[j][1].num) {
          destinyDevice = maxPath.current[j][0];
        } else {
          destinyDevice = maxPath.current[j][1];
        }

        await animateMessage(atualDevice.current.num, destinyDevice.num);
        atualDevice.current = destinyDevice;
        if (atualDevice.current.name === "switch") switchPass = true;
      }
      let connection = connections.find(
        ([pos1, pos2]) =>
          pos1.num === atualDevice.current!.num &&
          pos2.positionY > pos1.positionY &&
          pos2.hasDeffect == false
      );
      if (!connection) {
        connection = connections.find(
          ([pos1, pos2]) =>
            pos1.num === atualDevice.current!.num && pos2.positionY > pos1.positionY
        );
      }
      while (connection) {
        await animateMessage(atualDevice.current.num, connection[1].num);
        atualDevice.current = connection[1];
        if (atualDevice.current.hasDeffect) break;
        if (atualDevice.current.name === "switch") switchPass = true;
        connection = connections.find(
          ([pos1, pos2]) =>
            pos1.num === atualDevice.current!.num &&
            pos2.positionY > pos1.positionY &&
            pos2.hasDeffect == false
        );
        if (!connection) {
          connection = connections.find(
            ([pos1, pos2]) =>
              pos1.num === atualDevice.current!.num && pos2.positionY > pos1.positionY
          );
        }
      }

      if (atualDevice.current.hasDeffect) {
        if (onDeffect) onDeffect("ON_DEFFECT");
      } else if (atualDevice.current.positionY == 2) {
        if (!switchPass) {
          if (notSwitch) notSwitch("NOT_SWITCH");
        } else if (atualDevice.current.name != "modem") {
          if (onEndNotModem) onEndNotModem("END_NOT_MODEM");
        }
      } else {
        connection = connections.find(
          ([pos1, pos2]) =>
            (pos1.num == atualDevice.current!.num || pos2.num == atualDevice.current!.num) &&
            pos2.positionY == pos1.positionY
        );
        while (connection) {
          if (atualDevice.current.num == connection[1].num) {
            destinyDevice = connection[0];
          } else {
            destinyDevice = connection[1];
          }
          await animateMessage(atualDevice.current.num, destinyDevice.num);
          atualDevice.current = destinyDevice;

          if (atualDevice.current.hasDeffect) break;

          connection = connections.find(
            ([pos1, pos2]) =>
              pos1.num === atualDevice.current!.num &&
              pos2.positionY > pos1.positionY &&
              pos2.hasDeffect == false
          );

          if (!connection) {
            connection = connections.find(
              ([pos1, pos2]) =>
                pos1.num === atualDevice.current!.num && pos2.positionY > pos1.positionY
            );
            if (!connection) {
              connection = connections.find(
                ([pos1, pos2]) =>
                  (pos1.num === atualDevice.current!.num ||
                    pos2.num == atualDevice.current!.num) &&
                  pos2.positionY == pos1.positionY
              );
            }
          }
        }
        if (atualDevice.current.hasDeffect) {
          if (onDeffect) await onDeffect("ON_DEFFECT");
        } else if (!switchPass) {
          if (notSwitch) notSwitch("NOT_SWITCH");
        } else if (atualDevice.current.name != "modem" && atualDevice.current.positionY == 2) {
          if (onEndNotModem) onEndNotModem("END_NOT_MODEM");
        } else {
          await animateMessage(atualDevice.current.num, 0);
          if (onBackPlaying) onBackPlaying("BACK_TO_PLAYING");
        }
      }
    } else {
      const deffectPath = findFirstDeffectPath();
      if (deffectPath) {
        for (const connection of deffectPath) {
          if(connection[0].num == 0) continue;
          if (atualDevice.current.num == connection[1].num) {
            destinyDevice = connection[0];
          } else {
            destinyDevice = connection[1];
          }
          await animateMessage(atualDevice.current.num, destinyDevice.num);
          atualDevice.current = destinyDevice!;
          if (atualDevice.current.hasDeffect) break;
        }
        if (atualDevice.current.hasDeffect) {
          if (onDeffect) onDeffect("ON_DEFFECT");
          isFinalPath.current = 1;
        }
      } else {
        isFinalPath.current = 2;
        verifyPause();
      }
    }
  };

  const correctPath = async () => {
      await animateMessage(atualDevice.current!.num, modemConnection.current![1].num);
      atualDevice.current = modemConnection.current![1];
      for (let j = 0; j < most.current; j++) {
        if (atualDevice.current!.num == maxPath.current[j][1].num) {
          destinyDevice = maxPath.current[j][0];
        } else {
          destinyDevice = maxPath.current[j][1];
        }
        await animateMessage(atualDevice.current!.num, destinyDevice.num);
        atualDevice.current = destinyDevice;
        if (atualDevice.current.name === "switch") switchPass = true;
      }
      const finalConnection = connections.find(
        ([pos1, pos2]) => pos1.num === atualDevice.current!.num && pos2.num == 10
      );
      if (finalConnection) {
        await animateMessage(atualDevice.current!.num, 10);
        if (onComplete) onComplete("COMPLETE");
        return;
      }
      isFinalPath.current = 0;
      if (onEndNotModem) onEndNotModem("END_NOT_MODEM");
  }

  const findFirstDeffectPath = (): Connection[] | null => {
    const adjacencyList = new Map<number, [number, Connection][]>();
    const allNodes: Position[] = [...networkDevicesData];

    const getNodePosition = (
      nodeNum: number
    ): { x: number; y: number } | null => {
      const node = allNodes.find((n) => n.num === nodeNum);
      return node ? { x: node.positionX, y: node.positionY } : null;
    };

    connections.forEach((conn) => {
      const [pos1, pos2] = conn;
      if (!adjacencyList.has(pos1.num)) adjacencyList.set(pos1.num, []);
      adjacencyList.get(pos1.num)!.push([pos2.num, conn]);
    });

    // ALTERAÇÃO 1: Modificamos o DFS para aceitar o 'startNodeNum'
    // Isso serve para sabermos quem é o nó de origem na validação de movimento
    const dfs = (
      startNodeNum: number, // Novo parâmetro
      currentNodeNum: number,
      currentPath: Connection[],
      visitedInCurrentPath: Set<number>
    ): Connection[] | null => {
      if (visitedInCurrentPath.has(currentNodeNum)) {
        return null;
      }

      // Se achou um defeito (e não é o nó de início nem o 10/fim), retorna o caminho
      if (currentNodeNum !== startNodeNum && currentNodeNum !== 10) {
        const currentNode = allNodes.find((n) => n.num === currentNodeNum);
        if (currentNode?.hasDeffect) {
          return currentPath;
        }
      }

      const newVisited = new Set(visitedInCurrentPath).add(currentNodeNum);
      const neighbors = adjacencyList.get(currentNodeNum) || [];
      const currentPos = getNodePosition(currentNodeNum);

      for (const [neighborNum, connection] of neighbors) {
        const neighborPos = getNodePosition(neighborNum);
        if (currentPos && neighborPos) {
          if (currentNodeNum !== 0 && currentNodeNum !== startNodeNum) {
            if (neighborPos.y < currentPos.y) {
              continue;
            }
          }
        }

        const resultPath = dfs(
          startNodeNum, // Passa o ID de início adiante
          neighborNum,
          [...currentPath, connection],
          newVisited
        );
        
        if (resultPath) {
          return resultPath;
        }
      }
      return null;
    };

    const message = connections.find(c => c[0].num == 0)![0];
    const modem = allNodes.find(n => 
      n.name.includes('modem') && n.hasDeffect == false
      && n.positionY == 0
    );
    const path = dfs(modem!.num, modem!.num, [], new Set());
    if (path) {
        return [[message, modem!],...path];
    }
    

    return null;
  };

  const checkPathExists = (
    connections: Connection[],
    startNum: number,
    targetNum: number
  ): boolean => {
    const adjacencyList = new Map<number, number[]>();

    connections.forEach(([pos1, pos2]) => {
      const num1 = pos1.num;
      const num2 = pos2.num;
      if (!adjacencyList.has(num1)) {
        adjacencyList.set(num1, []);
      }
      adjacencyList.get(num1)!.push(num2);
      const isSpecialDevice = [0, 10].includes(num1) || [0, 10].includes(num2);

      if (isSpecialDevice) {
      } else {
        if (!adjacencyList.has(num2)) {
          adjacencyList.set(num2, []);
        }
        adjacencyList.get(num2)!.push(num1);
      }
    });
    const queue: number[] = [startNum];
    const visited = new Set<number>([startNum]);

    while (queue.length > 0) {
      const currentNum = queue.shift()!;

      if (currentNum === targetNum) {
        return true;
      }
      const uniqueNeighbors = new Set(adjacencyList.get(currentNum) || []);

      for (const neighborNum of uniqueNeighbors) {
        if (!visited.has(neighborNum)) {
          visited.add(neighborNum);
          queue.push(neighborNum);
        }
      }
    }
    return false;
  };

  const handleSubmit = async () => {
    if (state === StageState.SUBMITING || state === StageState.START_GAME) return;
    if (onSubmit) onSubmit("SUBMIT");
    if (connections.length === 0) {
      if (onNotConnected) onNotConnected("NOT_CONNECTED");
    } else if (
      !connections.some(([pos1, pos2]) => pos1.num == 0 && pos2.num != 0)
    ) {
      if (onFirstNotModem) onFirstNotModem("FIRST_NOT_MODEM");
    } else {
      if (checkPathExists(connections, 0, 10)) {
        await handleMessageAnimation();
        return;
      }
      if (onNotConnected)  await onNotConnected("NOT_CONNECTED");
    }
    if (onBackPlaying) onBackPlaying("BACK_TO_PLAYING");
  };

  return (
    <div className="relative w-full h-screen select-none">
      <canvas
        ref={canvasRef}
        style={{
          position: "absolute",
          top: 0,
          left: 0,
          zIndex: 10,
          width: "100%",
          height: "100%",
        }}
      />
      <motion.div
        animate={{ x: target.x, y: target.y }}
        transition={{
          type: "tween",
          ease: "linear",
        }}
        style={{
          position: "absolute",
          zIndex: 49,
          pointerEvents: "none",
          translateX: "-50%",
          translateY: "-50%",
        }}
        className="
                    w-[7.8%]
                    max-w-[91px]
                    h-auto
                "
      >
        {state === StageState.SUBMITING && (
          <Image
            src="/image/stage-3/message.svg"
            alt="message moving"
            width={0}
            height={0}
            className="w-full h-full object-contain"
          />
        )}
      </motion.div>
      <ButtonSend
        className="top-6 left-[50%] -translate-x-[50%] md:top-6 md:left-25 md:-translate-x-0 z-100"
        onClick={handleSubmit}
      >
        ENVIAR
      </ButtonSend>
      <div
        className="relative w-full h-screen bg-no-repeat bg-center bg-cover z-0"
        style={{ backgroundImage: `url(${imagePaths.details})` }}
      ></div>

      <div
        className="
                absolute
                left-1/2
                top-1/2
                -translate-x-1/2
                -translate-y-1/2
                w-[60%]
                max-w-[700px]
                max-h-[65vh]
                aspect-[8/6]
                z-20
            "
      >
        <div
          ref={(el) => {
            refs.current[0] = el;
          }}
          className="
                        absolute
                        aspect-[169/107]
                        w-[13%]
                        h-auto
                        left-[-25%]
                        bottom-[-18%]
                    "
        >
          {state != StageState.SUBMITING &&
            state != StageState.SHOW_COMPLETION_SCREEN && (
              <Image
                src="/image/stage-3/message.svg"
                alt="message"
                width={0}
                height={0}
                className="
                            w-full
                            h-full
                            cursor-pointer
                            transition-transform
                            active:scale-90
                        "
                onClick={(e) =>
                  handleClick(e, {
                    name: "message",
                    num: 0,
                    positionX: -1,
                    positionY: -1,
                  })
                }
              />
            )}
        </div>
        {networkDevicesData.map((device) => (
          <NetworkDevice
            key={device.num}
            ref={(el) => {
              refs.current[device.num] = el;
            }}
            posX={device.positionX}
            posY={device.positionY}
            name={device.name}
            hasDeffect={device.hasDeffect}
            onClick={(e) =>
              handleClick(e, {
                hasDeffect: device.hasDeffect,
                name: device.name,
                num: device.num,
                positionX: device.positionX,
                positionY: device.positionY,
              })
            }
          />
        ))}

        <div className="absolute right-[-25%] top-[-15%] w-[15%] h-[15%]">
          <Image
            src="image/stage-3/destiny.svg"
            alt="cesar"
            width={0}
            height={0}
            className="absolute top-[-40%] left-1/2 -translate-x-1/2 w-[40%] h-[40%]"
          />
          <NetworkDevice
            name="computer"
            className="w-full h-full"
            ref={(el) => {
              refs.current[10] = el;
            }}
            onClick={(e) =>
              handleClick(e, {
                hasDeffect: false,
                name: "computer",
                num: 10,
                positionX: -1,
                positionY: -1,
              })
            }
          />
        </div>
      </div>
    </div>
  );
}

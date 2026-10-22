"use client";

import { useState, FormEvent, useEffect } from 'react';
import { authenticateUser } from "@/domain/user/service/authService";
import { useRouter } from 'next/navigation';

type AuthState = 'Idle' | 'Editing' | 'ReadyToSubmit' | 'Submitting' | 'Error';

export const useAuth = () => {
    const [nickname, setNickname] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState<string | null>(null);
    const [authState, setAuthState] = useState<AuthState>('Idle');
    const router = useRouter();

    useEffect(() => {
        if (nickname.trim() === '' && password.trim() === '') {
            setAuthState('Idle');
        } else if (nickname.trim() !== '' && password.trim() !== '') {
            setAuthState('ReadyToSubmit');
        } else {
            setAuthState('Editing');
        }
    }, [nickname, password]);

    const handleAuthentication = async (event: FormEvent) => {
        event.preventDefault();

        if (authState !== 'ReadyToSubmit') return;

        setAuthState('Submitting');
        setErrorMessage(null);

        try {
            const response = await authenticateUser({ nickname, password });
            localStorage.setItem('accessToken', response.accessToken);
            localStorage.setItem('nickname', response.nickname);
            router.push('/stage-list');

        } catch (err) {
            setErrorMessage('Apelido ou senha inválidos.');
            setAuthState('Error');
            console.error(err);
        }
    };

    const handleNicknameChange = (value: string) => {
        setNickname(value);
        if (errorMessage) setErrorMessage(null);
    };

    const handlePasswordChange = (value: string) => {
        setPassword(value);
        if (errorMessage) setErrorMessage(null);
    };

    return {
        nickname,
        setNickname: handleNicknameChange,
        password,
        setPassword: handlePasswordChange,
        errorMessage,
        authState,
        handleAuthentication,
    };
};
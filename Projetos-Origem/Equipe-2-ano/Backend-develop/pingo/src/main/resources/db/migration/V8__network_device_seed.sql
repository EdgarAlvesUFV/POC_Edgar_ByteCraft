INSERT INTO NetworkDevices (
    name, 
    has_deffect, 
    type, 
    description, 
    created_at, 
    updated_at, 
    is_active
) VALUES 
-- Bloco 1 (Todos has_deffect = 0 e is_active = 1)
('computer', 0, 0, 'Estação de trabalho 1', NOW(), NOW(), 1),
('computer', 0, 1, 'Estação de trabalho 2', NOW(), NOW(), 1),
('computer', 0, 2, 'Estação de trabalho 3', NOW(), NOW(), 1),

('server', 0, 0, 'Servidor de Aplicações A', NOW(), NOW(), 1),
('server', 0, 1, 'Servidor de Aplicações B', NOW(), NOW(), 1),
('server', 0, 2, 'Servidor de Aplicações C', NOW(), NOW(), 1),

('modem', 0, 0, 'Modem ', NOW(), NOW(), 1),
('modem', 0, 1, 'Modem de Redundância', NOW(), NOW(), 1),
('modem', 0, 2, 'Modem de Redundância', NOW(), NOW(), 1),

('switch', 0, 1, 'Switch do Andar 1', NOW(), NOW(), 1),

('notebook', 0, 0, 'Notebook Executivo 1', NOW(), NOW(), 1),
('notebook', 0, 1, 'Notebook Executivo 2', NOW(), NOW(), 1),
('notebook', 0, 2, 'Notebook Executivo 3', NOW(), NOW(), 1),

('tablet', 0, 0, 'Tablet A', NOW(), NOW(), 1),
('tablet', 0, 1, 'Tablet B', NOW(), NOW(), 1),
('tablet', 0, 2, 'Tablet C', NOW(), NOW(), 1),

('antenna', 0, 0, 'Antena Inicial Externa', NOW(), NOW(), 1),
('antenna', 0, 1, 'Antena Central Externa', NOW(), NOW(), 1),
('antenna', 0, 2, 'Antena Final Externa', NOW(), NOW(), 1),

('smartphone', 0, 0, 'Smartphone de Logística', NOW(), NOW(), 1),
('smartphone', 0, 1, 'Smartphone de Games', NOW(), NOW(), 1),
('smartphone', 0, 2, 'Smartphone de pedra calcária', NOW(), NOW(), 1),

--

('computer', 1, 0, 'Estação de trabalho 1 Quebrada', NOW(), NOW(), 1),
('computer', 1, 1, 'Estação de trabalho 2 Quebrada', NOW(), NOW(), 1),
('computer', 1, 2, 'Estação de trabalho 3 Quebrada', NOW(), NOW(), 1),

('server', 1, 0, 'Servidor de Aplicações A Quebrado', NOW(), NOW(), 1),
('server', 1, 1, 'Servidor de Aplicações B Quebrado', NOW(), NOW(), 1),
('server', 1, 2, 'Servidor de Aplicações C Quebrado', NOW(), NOW(), 1),

('modem', 1, 0, 'Modem Quebrado', NOW(), NOW(), 1),
('modem', 1, 1, 'Modem Quebrado', NOW(), NOW(), 1),
('modem', 1, 2, 'Modem Quebrado', NOW(), NOW(), 1),

('switch', 1, 0, 'Switch do Andar 1 Quebrado', NOW(), NOW(), 1),
('switch', 1, 1, 'Switch do Andar 5 Quebrado', NOW(), NOW(), 1),
('switch', 1, 2, 'Switch do Andar 3 Quebrado', NOW(), NOW(), 1),

('notebook', 1, 0, 'Notebook Executivo B Quebrado', NOW(), NOW(), 1),
('notebook', 1, 1, 'Notebook Executivo C Quebrado', NOW(), NOW(), 1),
('notebook', 1, 2, 'Notebook Executivo V Quebrado', NOW(), NOW(), 1),

('tablet', 1, 0, 'Tablet Quebrado', NOW(), NOW(), 1),
('tablet', 1, 1, 'Tablet Quebrado', NOW(), NOW(), 1),
('tablet', 1, 2, 'Tablet Quebrado', NOW(), NOW(), 1),

('antenna', 1, 0, 'Antena Quebrada', NOW(), NOW(), 1),
('antenna', 1, 1, 'Antena Quebrada', NOW(), NOW(), 1),
('antenna', 1, 2, 'Antena Quebrada', NOW(), NOW(), 1),

('smartphone', 1, 0, 'Smartphone de Logística 4 Quebrado', NOW(), NOW(), 1),
('smartphone', 1, 1, 'Smartphone de Logística 5 Quebrado', NOW(), NOW(), 1),
('smartphone', 1, 2, 'Smartphone de Logística 6 Quebrado', NOW(), NOW(), 1
);
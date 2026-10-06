-- =============================================================================
-- CREATION OF TABLES
-- =============================================================================

-- 1. Patients Table
CREATE TABLE patients (
                          id UUID PRIMARY KEY,
                          full_name VARCHAR(150) NOT NULL,
                          cpf VARCHAR(11) NOT NULL UNIQUE,
                          birth_date DATE NOT NULL,
                          phone VARCHAR(20) NOT NULL,
                          email VARCHAR(255) NOT NULL UNIQUE,
                          created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                          updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

-- 2. Doctors Table
CREATE TABLE doctors (
                         id UUID PRIMARY KEY,
                         full_name VARCHAR(150) NOT NULL,
                         specialty VARCHAR(100) NOT NULL,
                         crm VARCHAR(20) NOT NULL UNIQUE,
                         created_at TIMESTAMP NOT NULL,
                         updated_at TIMESTAMP NOT NULL
);

-- 3. Appointments Table
CREATE TABLE appointments (
                              id UUID PRIMARY KEY,
                              patient_id UUID NOT NULL,
                              doctor_id UUID NOT NULL,
                              appointment_date_time TIMESTAMP NOT NULL,
                              status VARCHAR(20) NOT NULL,
                              cancellation_reason VARCHAR(255),
                              created_at TIMESTAMP NOT NULL,
                              updated_at TIMESTAMP NOT NULL,

                              CONSTRAINT fk_appointments_patient FOREIGN KEY (patient_id) REFERENCES patients(id),
                              CONSTRAINT fk_appointments_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id),
                              CONSTRAINT uk_doctor_appointment_time UNIQUE (doctor_id, appointment_date_time)
);

-- =============================================================================
-- SEED DATA: INITIAL DOCTORS
-- =============================================================================

INSERT INTO doctors (id, full_name, specialty, crm, created_at, updated_at)
VALUES
    -- 1. Clínico Geral
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Dr. Roberto Carlos Andrade', 'Clínica Médica', 'CRM/SC 12345', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 2. Cardiologista
    ('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'Dra. Fernanda Lima Vasconcelos', 'Cardiologia', 'CRM/SC 67890', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 3. Endocrinologista
    ('c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a33', 'Dr. Marcelo Mendonça Siqueira', 'Endocrinologia', 'CRM/SC 54321', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
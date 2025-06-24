-- -------------------------------------------------------------
-- MySQL 8 Database Schema for Electric Mobility Service Provider
-- -------------------------------------------------------------

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS emsp_system;

-- Use the created database
USE emsp_system;

-- Drop tables if they exist to ensure a clean slate
DROP TABLE IF EXISTS connectors;
DROP TABLE IF EXISTS evses;
DROP TABLE IF EXISTS locations;

-- Table for Locations (Charging Sites)
CREATE TABLE locations (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           address VARCHAR(255) NOT NULL,

    -- Coordinates (embedded value object)
                           latitude DOUBLE NOT NULL,
                           longitude DOUBLE NOT NULL,

    -- BusinessHours (embedded value object)
                           business_hours_opens_at TIME NOT NULL,
                           business_hours_closes_at TIME NOT NULL,

    -- Audit field: Automatically updated on creation and modification
                           last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,

                           INDEX idx_locations_last_updated (last_updated) -- Index for time-based queries
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table for EVSEs (Electric Vehicle Supply Equipment)
CREATE TABLE evses (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       location_id BIGINT NOT NULL, -- Foreign key to locations table
                       evse_id_value VARCHAR(255) UNIQUE NOT NULL, -- OCPI-compliant EVSE ID, unique across all EVSEs
                       status VARCHAR(50) NOT NULL, -- e.g., 'AVAILABLE', 'BLOCKED', 'INOPERATIVE', 'REMOVED'

    -- Audit field: Automatically updated on creation and modification
                       last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,

    -- Foreign key constraint with cascade on delete
                       CONSTRAINT fk_evses_location_id
                           FOREIGN KEY (location_id)
                               REFERENCES locations(id)
                               ON DELETE CASCADE,

                       INDEX idx_evses_location_id (location_id), -- Index for joining with locations
                       INDEX idx_evses_status (status), -- Index for status-based queries
                       INDEX idx_evses_last_updated (last_updated) -- Index for time-based queries
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table for Connectors (Physical Charging Ports)
CREATE TABLE connectors (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            evse_id BIGINT NOT NULL, -- Foreign key to evses table
                            standard VARCHAR(100) NOT NULL, -- e.g., 'IEC_62196_T2', 'CHADEMO'
                            power_level DOUBLE NOT NULL, -- Power in kW
                            voltage DOUBLE NOT NULL, -- Voltage in Volts

    -- Audit field: Automatically updated on creation and modification
                            last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,

    -- Foreign key constraint with cascade on delete
                            CONSTRAINT fk_connectors_evse_id
                                FOREIGN KEY (evse_id)
                                    REFERENCES evses(id)
                                    ON DELETE CASCADE,

                            INDEX idx_connectors_evse_id (evse_id) -- Index for joining with evses
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

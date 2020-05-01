CREATE TABLE userdb.users (
    id bigserial PRIMARY KEY,
    username VARCHAR (255) UNIQUE NOT NULL,
    name VARCHAR (512) NOT NULL,
    password VARCHAR (512) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
)

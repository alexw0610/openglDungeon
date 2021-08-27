CREATE TABLE IF NOT EXISTS CONNECTED_ACCOUNTS (
user_account_id SERIAL UNIQUE,
connected_ip_address VARCHAR(32) NOT NULL,
connected_udp_port VARCHAR(8) NOT NULL,
created timestamp,
last_changed timestamp);

DO $$ DECLARE
    tr_exists integer := (SELECT 1 FROM information_schema.triggers WHERE trigger_name = lower('TR_CONNECTED_ACCOUNTS_CREATED'));
BEGIN
    IF tr_exists = 1 THEN
       		RAISE NOTICE 'TRIGGER ALREADY EXISTS';
		ELSE
            CREATE TRIGGER TR_CONNECTED_ACCOUNTS_CREATED BEFORE INSERT
            ON CONNECTED_ACCOUNTS FOR EACH ROW EXECUTE PROCEDURE row_created();
    END IF;
END $$;

DO $$ DECLARE
    tr_exists integer := (SELECT 1 FROM information_schema.triggers WHERE trigger_name = lower('TR_CONNECTED_ACCOUNTS_LAST_CHANGED'));
BEGIN
    IF tr_exists = 1 THEN
       		RAISE NOTICE 'TRIGGER ALREADY EXISTS';
		ELSE
            CREATE TRIGGER TR_CONNECTED_ACCOUNTS_LAST_CHANGED BEFORE UPDATE
            ON CONNECTED_ACCOUNTS FOR EACH ROW EXECUTE PROCEDURE row_changed();
    END IF;
END $$;

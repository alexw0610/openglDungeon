CREATE OR REPLACE FUNCTION row_created() RETURNS TRIGGER
    LANGUAGE plpgsql
    AS $$
BEGIN
  NEW.created := current_timestamp;
  RETURN NEW;
END;
$$;

CREATE OR REPLACE FUNCTION row_changed() RETURNS TRIGGER
    LANGUAGE plpgsql
    AS $$
BEGIN
  NEW.last_changed := current_timestamp;
  RETURN NEW;
END;
$$;
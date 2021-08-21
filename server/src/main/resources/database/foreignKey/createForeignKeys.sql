ALTER TABLE CHARACTER_LOCATIONS
ADD FOREIGN KEY (character_id)
references CHARACTERS(character_id);

ALTER TABLE CHARACTERS
ADD FOREIGN KEY (user_account_id)
references ACCOUNTS(user_account_id);

ALTER TABLE CONNECTED_ACCOUNTS
ADD FOREIGN KEY (user_account_id)
references ACCOUNTS(user_account_id);
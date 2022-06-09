# Strays-Fullstack-Tribes

# TRIBES DATABASE TABLES GUIDE

## App User :person_fencing:
* **id (Long)** : unique, not null, auto-generated user id
* **username (String)** : unique, not null, user's username
* **email (String)** : unique, not null, user's email used for registration
* **password (String)** : not null, user's password
* **active (Boolean)** : not null, changes to true after user clicks on confirmation email
* **registrationToken (Long)** : not null, sent to user when registering, needed to complete registration (given in Unix timestamp)
* **registrationTokenExpirationDate (Long)** : not null, valid for 24 hours (given in Unix timestamp)
* **forgottenPasswordToken (Long)** : not using (given in Unix timestamp)
* **forgottenPasswordTokenExpirationDate (Long)** : not using (given in Unix timestamp)
* **userRole (UserRole - enum)** :
1. ADMIN ("admin")
2. PLAYER ("player")

## Battle :gun:
* **id (Long)** : unique, not null, auto-generated battle id
* **battleType** : type of battle (BattleType enum)
* **armyLeftKingdomTime (Long)** : not null, time when army left its kingdom (given in Unix timestamp)
* **battleTime (Long)** : not null, battle duration (given in Unix timestamp)
* **returnTime (Long)** : when will attacker troops come back / attacker see the battle result
* **attackerKingdom (Kingdom_id Long)** : not null, attacker kingdom's id
* **defenderKingdom (Kingdom_id Long)** : not null, defender kingdom's id
* **goldStolen (Long)** : amount of stolen gold when succesfull attack
* **foodStolen (Long)** : amount of stolen food when succesfull attack

## Building :hut:
* **id (Long)** : unique, not null, auto-generated building id
* **kingdom (Long Kingdom_id)** : unique, not null, assigned from Kingdom table
* **type (BuildingType - enum, String)** :
1. TOWN_HALL("town_hall")
2. BARRACKS("barracks")
3. ACADEMY("academy")
4. FARM("farm")
5. GOLD_MINE("gold_mine")
6. WALLS("walls")
7. MARKETPLACE("marketplace")
8. HIDEOUT("hideout")
9. MERCENARIES_INN("mercenaries_inn")
* **level (Integer)** : not null, building's level, calculated based on yaml rules file
* **buildingPosition (Integer)** : not null, possible positions defined in yaml rules file
* **constructTime (Long)** : total building construction time (given in Unix timestamp)
* **destroyTime(Long)** : building destruction time (given in Unix timestamp)
* **battle (Battle_id Long)** : battle where the building was lost

## Chat :envelope:
* **id (Long)** : unique, not null, auto-generated chat id
* **subject (String)** : not null, chat subject name
* **ownerId (AppUser_id Long)** : not null, chat owner's id

## ChatMember :envelope_with_arrow:
* **(Long)** : unique, not null, auto-generated chat member id
* **chatId (Chat_Id Long)** not null, unique chat id
* **memberId (AppUser_Id Long)** not null, chat user's id
* **lastViewed (LocalDateTime)** when chat was last viewed by chat user

## Kingdom :crown:
* **id (Long)** : unique, not null, auto-generated kingdom id
* **name (String)** : not null, kingdom's name created by user
* **corX (Long)** : not null, coordinate X on kingdom's map
* **corY (Long)** : not null, coordinate Y on kingdom's map
* **appUser (AppUser_id Long)** : not null, kingdom owner's id
* **goldAmount (Long)** : not null, kingdom's total amount of gold, calculated based on yaml rules file
* **goldProduction (Integer)** : not null, kingdom's gold production level, calculated based on yaml rules file
* **foodProduction (Integer)** : not null, kingdom's food production level, calculated based on yaml rules file
* **updatedAt (Long)** : not null, kingdom last update time (given in Unix timestamp)

## Message :postal_horn:
* **id (Long)** : unique, not null, auto-generated message id
* **chatId (Chat_Id Long)** : not null, chat id
* **text (String)** : not null, chat text (the actual message)
* **authorId (AppUser_id Long)** : not null, message author id
* **submitted (LocalDateTime)** : message send time

## Troop :military_helmet:
* **id (Long)** : unique, not null, auto-generated troop id
* **troopType (TroopType enum - String)** :
1. PHALANX("phalanx")
2. FOOTMAN("footman")
3. SCOUT("scout")
4. KNIGHT("knight")
5. TREBUCHET("trebuchet")
6. DIPLOMAT("diplomat")
7. SETTLER("settler")
* **endOfTrainingTime (Long)** : troop completed training info (given in Unix timestamp)  
* **destroyTime (Long)** : time of troop's death (given in Unix timestamp)
* **kingdom (Kingdom_id Long)** : not null, troop's kingdom id
* **battle (Battle_id Long)** : not null, troop's battle id

## UnitLevel :stadium:
* **id (Long)** : unique, not null, auto-generated unit level id
* **kingdom (Kingdom_id Long)** : id of kingdom that owns a particular training academy  
* **troopType (TroopType enum - String)** :
1. PHALANX("phalanx")
2. FOOTMAN("footman")
3. SCOUT("scout")
4. KNIGHT("knight")
5. TREBUCHET("trebuchet")
6. DIPLOMAT("diplomat")
7. SETTLER("settler")
* **upgradeLevel (Integer)** : not null, trainee's updgrade level
* **upgradeFinishedAt (Long)** : trainee training completion time (given in Unix timestamp)

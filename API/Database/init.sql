CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS "parking" (
  "id" UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  "name" VARCHAR,
  "latitude" VARCHAR,
  "longitude" VARCHAR,
  "total_capacity" INTEGER,
  "occupied_places" INTEGER,
  "gate_mode" VARCHAR
);


CREATE TABLE IF NOT EXISTS "admins" (
  "id" uuid
);

CREATE TABLE IF NOT EXISTS "movements" (
  "id" UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  "parking" uuid,
  "datetime" TIMESTAMP,
  "type" varchar
);

CREATE TABLE IF NOT EXISTS "follow_notifications" (
  "id" UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  "user_id" VARCHAR,
  "parking_id" uuid,
  "notification_id" VARCHAR
);

CREATE TABLE IF NOT EXISTS "history" (
  "id" UUID DEFAULT gen_random_uuid() PRIMARY KEY,
  "datetime" TIMESTAMP,
  "occupacy" FLOAT
);

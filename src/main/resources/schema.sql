DROP TABLE IF EXISTS bookings, comments, items, users, requests CASCADE;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(60) NOT NULL,
  email VARCHAR(20) NOT NULL,
  CONSTRAINT pk_user PRIMARY KEY (id),
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(60),
  description VARCHAR(200) NOT NULL,
  requestor_id BIGINT REFERENCES users(id) ON DELETE CASCADE NOT NULL,
  created TIMESTAMP without time zone,
  CONSTRAINT pk_request PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS items (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(60) NOT NULL,
  description VARCHAR(200) NOT NULL,
  available BOOLEAN,
  owner_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
  request_id BIGINT REFERENCES requests(id) ON DELETE CASCADE,
  CONSTRAINT pk_item PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS bookings (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  start_date TIMESTAMP without time zone,
  end_date TIMESTAMP without time zone,
  item_id BIGINT REFERENCES items(id) ON DELETE CASCADE,
  booker_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
  status VARCHAR(15) NOT NULL,
  CONSTRAINT pk_booking PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text VARCHAR(250) NOT NULL,
    item_id BIGINT REFERENCES items(id) ON DELETE CASCADE,
    author_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    created TIMESTAMP without time zone NOT NULL,
    CONSTRAINT pk_comment PRIMARY KEY (id)
);
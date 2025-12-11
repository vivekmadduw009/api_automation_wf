#!/bin/bash

echo "Waiting for PostgreSQL config files to appear inside backend container..."

# Wait for postgresql.conf to exist
while ! docker exec backend bash -c "[ -f /var/lib/postgresql/data/postgresql.conf ]"; do
  echo "PostgreSQL not ready yet... retrying in 7 seconds"
  sleep 7
done

echo "PostgreSQL config found!"
echo "Patching PostgreSQL configs..."

# Append pg_hba.conf rule
docker exec backend bash -c "echo \"host all all 0.0.0.0/0 md5\" >> /var/lib/postgresql/data/pg_hba.conf"

# Fix listen_addresses
docker exec backend bash -c "sed -i \"s/^#listen_addresses *= *.*/listen_addresses = '*' /\" /var/lib/postgresql/data/postgresql.conf"

docker exec backend bash -c "sed -i \"s/listen_addresses *= *'localhost'/listen_addresses = '*' /\" /var/lib/postgresql/data/postgresql.conf"

echo "Config patched. Restarting backend container..."
docker restart backend

echo "Done!"
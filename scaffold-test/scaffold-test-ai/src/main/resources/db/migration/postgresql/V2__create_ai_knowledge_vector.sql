CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS public.ai_knowledge_vector (
    id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
    content text,
    metadata json,
    embedding vector(512)
);

CREATE INDEX IF NOT EXISTS ai_knowledge_vector_embedding_idx
    ON public.ai_knowledge_vector
    USING HNSW (embedding vector_cosine_ops);

ALTER TABLE public.ai_knowledge_vector
    ALTER COLUMN id DROP DEFAULT,
    ALTER COLUMN id TYPE text USING id::text;

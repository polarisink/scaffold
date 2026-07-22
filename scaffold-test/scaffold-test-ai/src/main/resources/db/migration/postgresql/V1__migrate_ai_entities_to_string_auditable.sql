DO $$
BEGIN
    IF to_regclass('public.ai_demo_order') IS NOT NULL THEN
        ALTER TABLE ai_demo_order ADD COLUMN IF NOT EXISTS id varchar(255);
        ALTER TABLE ai_demo_order ADD COLUMN IF NOT EXISTS deleted integer;

        UPDATE ai_demo_order SET id = order_no WHERE id IS NULL;
        UPDATE ai_demo_order SET deleted = 0 WHERE deleted IS NULL;

        ALTER TABLE ai_demo_order ALTER COLUMN id SET NOT NULL;
        ALTER TABLE ai_demo_order ALTER COLUMN deleted SET NOT NULL;
        ALTER TABLE ai_demo_order DROP CONSTRAINT IF EXISTS ai_demo_order_pkey;
        ALTER TABLE ai_demo_order ADD CONSTRAINT ai_demo_order_pkey PRIMARY KEY (id);

        IF NOT EXISTS (
            SELECT 1 FROM pg_constraint WHERE conname = 'uk_ai_demo_order_order_no'
        ) THEN
            ALTER TABLE ai_demo_order
                ADD CONSTRAINT uk_ai_demo_order_order_no UNIQUE (order_no);
        END IF;
    END IF;

    IF to_regclass('public.ai_demo_logistics') IS NOT NULL THEN
        ALTER TABLE ai_demo_logistics ADD COLUMN IF NOT EXISTS id varchar(255);
        ALTER TABLE ai_demo_logistics ADD COLUMN IF NOT EXISTS deleted integer;

        UPDATE ai_demo_logistics SET id = order_no WHERE id IS NULL;
        UPDATE ai_demo_logistics SET deleted = 0 WHERE deleted IS NULL;

        ALTER TABLE ai_demo_logistics ALTER COLUMN id SET NOT NULL;
        ALTER TABLE ai_demo_logistics ALTER COLUMN deleted SET NOT NULL;
        ALTER TABLE ai_demo_logistics DROP CONSTRAINT IF EXISTS ai_demo_logistics_pkey;
        ALTER TABLE ai_demo_logistics ADD CONSTRAINT ai_demo_logistics_pkey PRIMARY KEY (id);

        IF NOT EXISTS (
            SELECT 1 FROM pg_constraint WHERE conname = 'uk_ai_demo_logistics_order_no'
        ) THEN
            ALTER TABLE ai_demo_logistics
                ADD CONSTRAINT uk_ai_demo_logistics_order_no UNIQUE (order_no);
        END IF;
    END IF;

    IF to_regclass('public.ai_knowledge_document') IS NOT NULL THEN
        ALTER TABLE ai_knowledge_document ADD COLUMN IF NOT EXISTS id varchar(255);
        ALTER TABLE ai_knowledge_document ADD COLUMN IF NOT EXISTS deleted integer;

        UPDATE ai_knowledge_document SET id = document_id WHERE id IS NULL;
        UPDATE ai_knowledge_document SET deleted = 0 WHERE deleted IS NULL;

        ALTER TABLE ai_knowledge_document ALTER COLUMN id SET NOT NULL;
        ALTER TABLE ai_knowledge_document ALTER COLUMN deleted SET NOT NULL;
        ALTER TABLE ai_knowledge_document DROP CONSTRAINT IF EXISTS ai_knowledge_document_pkey;
        ALTER TABLE ai_knowledge_document ADD CONSTRAINT ai_knowledge_document_pkey PRIMARY KEY (id);

        IF NOT EXISTS (
            SELECT 1 FROM pg_constraint WHERE conname = 'uk_ai_knowledge_document_document_id'
        ) THEN
            ALTER TABLE ai_knowledge_document
                ADD CONSTRAINT uk_ai_knowledge_document_document_id UNIQUE (document_id);
        END IF;
    END IF;
END
$$;

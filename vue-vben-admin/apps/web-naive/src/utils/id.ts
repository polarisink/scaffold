export type IdValue = number | string;

export function toNumberId(value: IdValue | null | undefined) {
  if (value === null || value === undefined || value === '') {
    return null;
  }
  const numberValue = Number(value);
  return Number.isFinite(numberValue) ? numberValue : null;
}

export function toNumberIds(values: Array<IdValue | null | undefined>) {
  return values
    .map((value) => toNumberId(value))
    .filter((value): value is number => value !== null);
}

export function normalizeTreeIds<
  T extends {
    children?: T[];
    id: IdValue;
    parentId?: IdValue;
  },
>(nodes: T[]): T[] {
  return nodes.map((node) => {
    const normalized = {
      ...node,
      id: toNumberId(node.id) ?? node.id,
      parentId:
        node.parentId === undefined
          ? undefined
          : (toNumberId(node.parentId) ?? node.parentId),
      children: node.children ? normalizeTreeIds(node.children) : undefined,
    };
    return normalized as T;
  });
}

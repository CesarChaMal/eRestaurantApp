export interface ICategories {
  id?: string;
  description?: string | null;
}

export class Categories implements ICategories {
  constructor(public id?: string, public description?: string | null) {}
}

export function getCategoriesIdentifier(categories: ICategories): string | undefined {
  return categories.id;
}

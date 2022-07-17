export interface IAd {
  id?: string;
  url?: string;
  description?: string | null;
}

export class Ad implements IAd {
  constructor(public id?: string, public url?: string, public description?: string | null) {}
}

export function getAdIdentifier(ad: IAd): string | undefined {
  return ad.id;
}

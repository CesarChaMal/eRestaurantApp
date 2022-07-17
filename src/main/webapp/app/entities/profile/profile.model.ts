export interface IProfile {
  id?: string;
  description?: string | null;
}

export class Profile implements IProfile {
  constructor(public id?: string, public description?: string | null) {}
}

export function getProfileIdentifier(profile: IProfile): string | undefined {
  return profile.id;
}

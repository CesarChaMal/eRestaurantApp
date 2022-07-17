export interface IState {
  id?: string;
  description?: string | null;
}

export class State implements IState {
  constructor(public id?: string, public description?: string | null) {}
}

export function getStateIdentifier(state: IState): string | undefined {
  return state.id;
}

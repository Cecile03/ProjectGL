// Here declare global type which can be use in the whole app

export type Criteria = {
  id: number;
  minAverageThreshold: number;
  numberOfBachelor: number;
  numberOfGirls: number;
  numberOfTeams: number;
};
export type Team = {
  id: number;
  name: string;
  status: 'none' | 'publish' | 'prepublish';
  supervisor: User;
  users: User[];
  criteria: Criteria;
  validated?: boolean;
};

export type UserRole = 'SS' | 'PL' | 'OL' | 'OS' | 'TC';

export type User = {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  roles: UserRole[];
  gender: 'male' | 'female'; // precise more with enum?
  option: string;
  isBachelor: boolean;
  gradePast: number;
  team?: Team;
};

export interface UserData {
  id: number | undefined;
  name: string;
  mfb: string;
  average: number;
  team: number;
}
//Typage pour LES TABLEAUX !!!!!!!!!!
export interface TeamData {
  users: UserData[];
  id?: number;
  name: string;
  status: 'none' | 'publish' | 'prepublish';
  supervisor: User;
  criteria: any;
  validated?: boolean;
}

export interface Flag {
  id?: number;
  user: User;
  team1: Team;
  team2: Team;
  comment: string;
  datetime: Date;
}

export interface UserFlag {
  id?: number;
  flag: Flag;
  user: User;
  teamSwitched: boolean;
  validated: boolean | null;
  canceledString: string | null;
}

export interface Sprint {
  id: number;
  startDate: Date;
  endDate: Date;
  endType: string;
}

export interface Comment {
  id?: number;
  title: string;
  content: string;
  emitter: Partial<User>;
  team: Partial<TeamData>;
  date?: Date;
  sprint: Partial<Sprint>;
}

export interface Feedback {
  id?: number;
  title: string;
  content: string;
  emitter: Partial<User>;
  team: Partial<TeamData>;
  date?: Date;
  sprint: Partial<Sprint>;
}
export interface ProjectGrade {
  validated: boolean;
  id: number;
  value: number;
  initialGrade: InitialGrade;
  workGrade: WorkGrade;
  presentationGrade: PresentationGrade;
  sprint: Sprint;
  user: User;
}
export interface InitialGrade {
  id: number;
  value: number;
  prmo: SubGrade;
  spco: SubGrade;
  teso: SubGrade;
  supr: SubGrade;
  sprint: Sprint;
  user: User;
}
export interface WorkGrade {
  id: number;
  value: number;
  tebm: SubGrade;
  ssbm: SubGrade;
  sprint: Sprint;
  user: User;
}
export interface PresentationGrade {
  id: number;
  value: number;
  sspr: SubGrade;
  otpr: SubGrade;
  tcpr: SubGrade;
  sprint: Sprint;
  user: User;
}

export interface SubGrade {
  id: number;
  value: number;
  type: string;
  status: string;
  sprint: Sprint;
  user: User;
  evaluators: any;
  gradeType: GradeType,
}

export interface Evaluation {
  id: number;
  value: number;
  status: 'PENDING' | 'COMPLETED' | 'IN_PROGRESS';
  subGrade: SubGrade;
  evaluator: User;
}

export type Notification = {
  id?: number;
  type: string;
  status: 'READ' | 'UNREAD';
  description: string;
  date: Date;
  emitter: User;
  receiver: User;
  groupeId?: number;
};

export type NotificationSend = {
  id?: number;
  type: string;
  status: 'READ' | 'UNREAD';
  description: string;
  date: Date;
  emitterId: number;
  receiverId: number;
  groupeId?: number;
};

export interface GradeType {
  id: number;
  name: string;
  description: string;
}

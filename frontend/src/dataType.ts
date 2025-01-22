import { Key } from "react";

export type PostCardsType = {
  career: number;
  position: string;
  user_name: string;
  resume_id: Key;
  tech_stack_names: string[];
};

export type ResumeType = {
  id: number;
  version: string;
  date: string;
};

export type BookmarkType = {
  id: number;
  resume_id: number;
  bookmark_id: number;
  user_name: string;
  title: string;
  date: string;
};

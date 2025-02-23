import { Key } from "react";

export type PostCardsType = {
  career: number;
  position: string;
  user_name: string;
  resume_id: Key;
  tech_stack_names: string[];
};

export type ResumeType = {
  resume_id: number;
  resume_name: string;
  user_name: string;
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

export type BookmarkListType = {
  bookmark_id: number;
  created_at: string;
  resume_author: string;
  resume_id: number;
  resume_title: string;
};

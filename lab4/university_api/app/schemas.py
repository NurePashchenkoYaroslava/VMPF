from pydantic import BaseModel, EmailStr
from datetime import date
from typing import List, Optional

class StudentCreate(BaseModel):
    name: str
    email: EmailStr

class StudentResponse(BaseModel):
    id: int
    name: str
    email: str
    class Config:
        from_attributes = True

class CourseCreate(BaseModel):
    title: str
    teacher_id: int

class CourseResponse(BaseModel):
    id: int
    title: int | str
    teacher_id: int
    class Config:
        from_attributes = True

class GradeCreate(BaseModel):
    student_id: int
    course_id: int
    score: int

class GradeResponse(BaseModel):
    id: int
    student_id: int
    course_id: int
    score: int
    class Config:
        from_attributes = True
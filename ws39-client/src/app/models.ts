export interface Marvel {
    id: number
    name: string
}

export interface Comments {
    comment: string
}

export interface Character {
    id: number
    name: string
    description: string
    imagePath: string
    comments: Comments[]
}
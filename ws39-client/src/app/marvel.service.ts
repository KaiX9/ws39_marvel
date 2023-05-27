import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Character, Marvel } from "./models";
import { Observable } from "rxjs";

const URL_CHARS = 'http://localhost:8080/api/characters'

@Injectable()
export class MarvelService {

    chars$!: Observable<Marvel[]>
    character$!: Observable<Character>
    character!: Character

    http = inject(HttpClient)

    getChars(name: string): Observable<Marvel[]> {
        const params = new HttpParams().set("name", name);
        this.chars$ = this.http.get<Marvel[]>(URL_CHARS, { params })
        return this.chars$;
    }

    getCharDetails(characterId: number): Observable<Character> {
        return this.http.get<Character>(`${URL_CHARS}/${characterId}`);
    }

    postComments(comments: string, characterId: number): Observable<any> {
        const c = {
            comments: comments
        }
        return this.http.post<any>(`${URL_CHARS}/${characterId}`, c);
    }

    setCharacter(character: Character) {
        this.character = character;
    }

    getCharacter() {
        return this.character;
    }

}
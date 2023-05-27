import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { MarvelService } from '../marvel.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Character, Comments } from '../models';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-chardetails',
  templateUrl: './chardetails.component.html',
  styleUrls: ['./chardetails.component.css']
})
export class ChardetailsComponent implements OnInit, OnDestroy {

  charId!: number
  character!: Character
  commentsList!: Comments[]
  charSub$!: Subscription
  marvelSvc = inject(MarvelService)
  activatedRoute = inject(ActivatedRoute)
  router = inject(Router)

  ngOnInit(): void {
    this.charId = this.activatedRoute.snapshot.params['id'];
    this.charSub$ = this.marvelSvc.getCharDetails(this.charId).subscribe(
      result => {
        console.info(result),
        this.character = result,
        this.commentsList = this.character.comments,
        this.marvelSvc.setCharacter(this.character);

      }
    );
  }

  stringify(obj: any): string {
    return JSON.stringify(obj);
  }

  inputComment() {
    this.router.navigate(['/characters', this.charId, 'comments']);
  }

  ngOnDestroy(): void {
      this.charSub$.unsubscribe();
  }

}

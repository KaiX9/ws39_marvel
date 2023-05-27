import { Component, ElementRef, OnInit, ViewChild, inject } from '@angular/core';
import { Location } from '@angular/common';
import { Character } from '../models';
import { MarvelService } from '../marvel.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-comments',
  templateUrl: './comments.component.html',
  styleUrls: ['./comments.component.css']
})
export class CommentsComponent implements OnInit {

  character!: Character
  commentsInput!: string
  marvelSvc = inject(MarvelService)
  location = inject(Location)
  router = inject(Router)

  @ViewChild('comments')
  comments!: ElementRef

  ngOnInit(): void {
    this.character = this.marvelSvc.getCharacter();
    console.info('in comments: ', this.character);
  }

  postComments() {
    this.commentsInput = this.comments.nativeElement.value;
    this.marvelSvc.postComments(this.commentsInput, this.character.id).subscribe(
      result => {
        alert(JSON.stringify(result));
      }
    );
    this.router.navigate(['/characters', this.character.id])
  }

  back() {
    this.location.back();
  }

}

import { Component, ElementRef, ViewChild, inject } from '@angular/core';
import { MarvelService } from '../marvel.service';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-namesearch',
  templateUrl: './namesearch.component.html',
  styleUrls: ['./namesearch.component.css']
})
export class NamesearchComponent {

  name: string = ''
  chars$!: Subscription
  marvelSvc = inject(MarvelService)
  router = inject(Router)
  reloaded = false;

  @ViewChild('name')
  nameInput!: ElementRef

  searchName() {
    this.name = this.nameInput.nativeElement.value;
    console.info('name: ', this.name);
    this.marvelSvc.getChars(this.name);
    this.router.navigate(['/characters']);
  }

}

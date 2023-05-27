import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { MarvelService } from '../marvel.service';
import {  Marvel } from '../models';
import { Observable, Subscription, of } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-charlist',
  templateUrl: './charlist.component.html',
  styleUrls: ['./charlist.component.css']
})
export class CharlistComponent implements OnInit, OnDestroy {

  chars$!: Observable<Marvel[]>
  charSub$!: Subscription
  charList: Marvel[] = []
  charId!: number
  currentPage: number = 1
  pageSize: number = 0

  marvelSvc = inject(MarvelService)
  router = inject(Router)

  ngOnInit(): void {
      this.chars$ = this.marvelSvc.chars$;
      this.charSub$ = this.chars$.subscribe(
        result => {
          this.charList = result;
          console.info(this.charList)
          this.updatePage();
        }
      )
  }

  updatePage() {
    const totalPages = Math.ceil(this.charList.length / 5);
    if (this.currentPage > totalPages) {
      this.currentPage = 1;
    }
    this.chars$ = of(
      this.charList.slice(
        (this.currentPage - 1) * 5, 
        this.currentPage * 5
      )
    );
  }

  prevPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
    this.updatePage();
  }

  nextPage() {
    const totalPages = Math.ceil(this.charList.length / 5);
    if (this.currentPage < totalPages) {
      this.currentPage++;
      this.updatePage();
    }
  }

  checkChar(i: number) {
    const index = (this.currentPage - 1) * 5 + i;
    this.charId = this.charList[index].id;
    console.info(this.charId);
    this.router.navigate(['/characters', this.charId]);
  }

  ngOnDestroy(): void {
      this.charSub$.unsubscribe();
  }

}

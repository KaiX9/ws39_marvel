import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';

import { AppComponent } from './app.component';
import { NamesearchComponent } from './components/namesearch.component';
import { CharlistComponent } from './components/charlist.component';
import { RouterModule, Routes } from '@angular/router';
import { MarvelService } from './marvel.service';
import { ChardetailsComponent } from './components/chardetails.component';
import { CommentsComponent } from './components/comments.component';

const routes: Routes = [
  { path: '', component: NamesearchComponent },
  { path: 'characters', component: CharlistComponent },
  { path: 'characters/:id', component: ChardetailsComponent },
  { path: 'characters/:id/comments', component: CommentsComponent },
  { path: '**', redirectTo: '/', pathMatch: 'full' }
]

@NgModule({
  declarations: [
    AppComponent,
    NamesearchComponent,
    CharlistComponent,
    ChardetailsComponent,
    CommentsComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    RouterModule.forRoot(routes, { useHash: true })
  ],
  providers: [MarvelService],
  bootstrap: [AppComponent]
})
export class AppModule { }

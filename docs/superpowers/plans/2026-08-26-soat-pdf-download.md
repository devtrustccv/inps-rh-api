# SOAT PDF download implementation plan

**Goal:** Add a read-only SOAT endpoint that renders the insurance form with Thymeleaf and returns a downloadable PDF.

**Architecture:** A query/handler receives the SOAT and policy UUIDs. `SoatService` assembles a PDF-specific model from
the selected SOAT, the active institution record, the selected active policy, and SOAT detail rows. The existing
`PdfGenerator` processes `soat-pdf.html` and Flying Saucer converts the resulting XHTML to PDF.

**Tech stack:** Spring Boot MVC, iGRP QueryBus, Spring Data JPA, Thymeleaf, Flying Saucer.

---

1. Add PDF view-model DTOs for header, institution, policy, totals, and insured-person rows.
2. Add repository lookups that fetch an active policy by UUID and SOAT details with the employee graph needed after the
   transaction ends.
3. Add `SoatService.gerarFicheiroSoat(...)` to validate active configuration, map the data, render `soat-pdf`, and
   return PDF bytes.
4. Add `DownloadSoatPdfQuery` and its handler, including `application/pdf`, content length, and a safe attachment
   filename.
5. Add `GET /processamento/soat/ficheiro?soatId=...&apoliceId=...` to `SoatController`.
6. Add the XHTML-compatible Thymeleaf template implementing specification section 3.6.4.
7. Add focused tests for mapping, PDF response headers/content, and controller dispatch; run targeted tests and Maven
   compile.

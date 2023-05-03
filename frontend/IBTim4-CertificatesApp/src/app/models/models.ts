export interface CertifDTO {
    id: number,
    startTime: string,
    endTime: string,
    subject: UserExpandedDTO,
    issuer: IssuerDTO,
    type: string
}

export interface UserExpandedDTO {
    name: string,
    lastName: string,
    email: string
}

export interface IssuerDTO {
    serialNumber: number,
    type: string
}
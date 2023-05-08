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
    phone?: string
    password?: string
}

export interface IssuerDTO {
    serialNumber: number,
    type: string
}

export interface TwilioDTO {
    phone:string
    code:string
}

export interface PasswordReset {
    sender:string
    type:string
}

export interface PasswordChangeDTO {
    email: string
    password: string
    phone:string
    code:string
}
